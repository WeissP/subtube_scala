package com.jb.algebras.youtube

import cats.*
import cats.data.*
import cats.effect.*
import cats.effect.kernel.{Async, Resource}
import cats.syntax.all.*
import com.jb.algebras.YoutubeQueryAlg
import com.jb.config.YoutubeQueryConfig
import com.jb.domain.*
import io.circe.generic.auto.*
import io.circe.{Decoder, HCursor, Json}
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.given
import io.github.iltotore.iron.{:|, RefinedTypeOps, refine}
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.client.middleware.FollowRedirect
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.headers.Accept
import org.http4s.{
  EntityDecoder,
  Headers,
  MediaType,
  Method,
  QueryParamEncoder,
  Request,
  Uri,
}
import org.typelevel.log4cats.Logger

case class VideosWithCont(
    videos: List[YoutubeMedia],
    continuation: Option[Continuation],
)

object Invidious {
  given Decoder[YoutubeMedia] = new Decoder[YoutubeMedia] {
    final def apply(c: HCursor): Decoder.Result[YoutubeMedia] = (
      c.get[MediaLength]("lengthSeconds"),
      c.get[String]("title"),
      c.get[List[Thumbnail]]("videoThumbnails"),
      c.get[YtbChannelID]("authorId"),
      c.get[YtbVideoID]("videoId"),
      c.get[UnixTS]("published"),
      c.get[String]("descriptionHtml"),
    ).mapN(YoutubeMedia.apply)
  }

  given Decoder[Channel] = new Decoder[Channel] {
    final def apply(c: HCursor): Decoder.Result[Channel] = (
      c.get[YtbChannelID]("authorId"),
      c.get[String]("author"),
      c.get[String]("descriptionHtml"),
      c.get[Int :| Positive]("subCount"),
      c.get[List[Thumbnail]]("authorThumbnails"),
    ).mapN(Channel.apply)
  }
  given QueryParamEncoder[Continuation] = QueryParamEncoder.fromShow

  private val videoFields =
    "title,videoId,authorId,descriptionHtml,videoThumbnails,published,lengthSeconds"

  def client[F[_]: Async: Logger](
      c: Client[F],
      cfg: YoutubeQueryConfig,
  ): YoutubeQueryAlg[F] = {
    val root = cfg.invidiousRoot / "api" / "v1"

    def videosByChannelRaw(
        id: YtbChannelID,
        fetchState: FetchState,
        order: MediaSortOrder,
    ): F[VideosWithCont] = {
      given EntityDecoder[F, VideosWithCont] = jsonOf[F, VideosWithCont]
      val uri = root / "channels" / id.value / "videos"
        +? ("fields", s"continuation,videos(${videoFields})")
        +? ("sort_by", order.toString().toLowerCase())
      fetchState match {
        case FetchState.Init => c.expect[VideosWithCont](uri)
        case FetchState.Done => VideosWithCont(List(), None).pure
        case FetchState.InProgress(cont) =>
          c.expect[VideosWithCont](uri +? ("continuation", cont))
      }
    }

    type ItemNum = Int
    type Fetch[T] = StateT[F, (ItemNum, FetchState), T]

    def videosByChannelState(
        id: YtbChannelID,
        order: MediaSortOrder,
    ): Fetch[List[YoutubeMedia]] = for {
      (len, cont) <- StateT.get
      VideosWithCont(videos, newCont) <- StateT.liftF(
        videosByChannelRaw(id, cont, order),
      )
      _ <- StateT.liftF(
        Logger[F].debug(
          s"new batch of channel videos fetched with length ${videos.length}",
        ),
      )
      _ <- StateT.set(
        (
          len + videos.length,
          newCont match
            case None       => FetchState.Done
            case Some(cont) => FetchState.InProgress(cont),
        ),
      )
    } yield videos

    new YoutubeQueryAlg[F] {
      def videosByChannel(
          id: YtbChannelID,
          order: MediaSortOrder,
          minNum: Int,
      ): F[List[YoutubeMedia]] = {
        val continue: Fetch[Boolean] = for {
          (itemNum, fs) <- StateT.get
        } yield (itemNum < minNum) && (fs != FetchState.Done)

        videosByChannelState(id, order)
          .whileM[List](continue)
          .runA((0, FetchState.Init))
          .map(_.flatten)
      }

      def videoInfo(id: YtbVideoID): F[YoutubeMedia] = {
        val req = Request[F](
          method = Method.GET,
          uri = root / "videos" / id.value +? ("fields", videoFields),
          headers = Headers(Accept(MediaType.application.json)),
        )
        c.expect[YoutubeMedia](req)
      }

      def channelInfo(id: YtbChannelID): F[Channel] = {
        val req = Request[F](
          method = Method.GET,
          uri =
            root / "channels" / id.value +? ("fields", "author,authorId,descriptionHtml,subCount,authorThumbnails"),
          headers = Headers(Accept(MediaType.application.json)),
        )
        c.expect[Channel](req)
      }

      def searchChannels(query: String): F[List[Channel]] = ???
    }
  }
}
