package com.jb.algebras.youtube

import cats.effect.kernel.Resource
import com.jb.algebras.YoutubeQueryAlg
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import cats.effect.kernel.Async
import com.jb.domain.*
import org.http4s.client.middleware.FollowRedirect
import org.http4s.Request
import org.http4s.Uri
import org.http4s.Method
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf
import io.github.iltotore.iron.circe.given
import org.http4s.circe.CirceEntityDecoder._
import io.circe.generic.auto.*
import org.http4s.headers.Accept
import org.http4s.MediaType
import org.http4s.Headers

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

  def client[F[_]: Async](c: Client[F], root: Uri): YoutubeQueryAlg[F] = {
    // given EntityDecoder[F, Channel] = jsonOf[F, Channel]
    // val clt = FollowRedirect(10, _ => true)(c)
    new YoutubeQueryAlg[F] {
      def videosByChannel(
          id: YtbChannelID,
          cont: Continuation,
          order: MediaSortOrder,
      ): F[List[MediaInfo[YoutubeMedia]]] = ???

      def channelInfo(id: YtbChannelID): F[Channel] = {
        // c.expect[Json](channelInfoReq(root)(id))
        c.expect(channelInfoReq(root)(id))(jsonOf[F, Channel])
      }

      def searchChannels(query: String): F[List[Channel]] = ???

    }
  }
}
