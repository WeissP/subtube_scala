package com.jb.algebras.youtube

import cats.effect.kernel.{Async, Resource}
import cats.syntax.all.*
import com.jb.algebras.YoutubeQueryAlg
import com.jb.domain.*
import io.circe.generic.auto.*
import io.circe.{Decoder, HCursor}
import io.github.iltotore.iron._
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.client.middleware.FollowRedirect
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.headers.Accept
import org.http4s.{EntityDecoder, Headers, MediaType, Method, Request, Uri}

object Invidious {
  def httpClient[F[_]: Async] = {
    EmberClientBuilder.default[F].build
  }

  // private val

  private def videosByChannel[F[_]: Async](root: Uri)(
      id: YtbChannelID,
      cont: Continuation,
      order: MediaSortOrder,
  ): Request[F] = {
    ???
  }

  private def channelInfoReq[F[_]: Async](root: Uri)(
      id: YtbChannelID,
  ): Request[F] = {
    Request[F](
      method = Method.GET,
      uri = root / "channels" / id.value,
      headers = Headers(Accept(MediaType.application.json)),
    )
  }

  given Decoder[Channel] = new Decoder[Channel] {
    final def apply(c: HCursor): Decoder.Result[Channel] = (
      c.get[YtbChannelID]("authorId"),
      c.get[String]("descriptionHtml"),
      c.get[Int :| Positive]("subCount"),
      c.get[List[Thumbnail]]("authorThumbnails"),
    ).mapN(Channel.apply)
  }

  def client[F[_]: Async](c: Client[F], root: Uri): YoutubeQueryAlg[F] = {
    new YoutubeQueryAlg[F] {
      def videosByChannel(
          id: YtbChannelID,
          cont: Continuation,
          order: MediaSortOrder,
      ): F[List[MediaInfo[YoutubeMedia]]] = ???

      def channelInfo(id: YtbChannelID): F[Channel] = {
        c.expect[Channel](channelInfoReq(root)(id))
      }

      def searchChannels(query: String): F[List[Channel]] = ???

    }
  }
}
