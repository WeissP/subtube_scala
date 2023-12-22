package com.jb.programs

import cats.effect.kernel.{Async, Clock}
import cats.syntax.all._
import cats.{Applicative, Functor, Monad, MonadThrow}
import com.jb.algebras.{Algebras, YoutubeQueryAlg}
import com.jb.domain.{CachedAt, ChannelInfo, ChannelMeta, UnixTS, YtbChannelID}
import io.github.iltotore.iron.*

final case class GetChannelInfo[F[_]: Async: MonadThrow: Functor](
    yqa: YoutubeQueryAlg[F],
) {
  def channelInfo(id: YtbChannelID): F[ChannelInfo] = {
    for {
      ch <- yqa.channelInfo(id)
      c <- CachedAt.now[F]
    } yield ChannelInfo(ch, ChannelMeta(c, None))
  }
}

sealed abstract class Programs[F[_]: Async] private (
    algebras: Algebras[F],
) {
  val getChannelInfo: GetChannelInfo[F] = GetChannelInfo(algebras.youtubeQueryAlg)
}

object Programs {
  def make[F[_]: Async](
      algebras: Algebras[F],
  ): Programs[F] =
    new Programs[F](algebras) {}
}
