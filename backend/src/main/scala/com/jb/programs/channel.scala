package com.jb.programs

import com.jb.algebras.YoutubeQueryAlg
import cats.effect.kernel.Async
import com.jb.domain.ChannelInfo
import com.jb.domain.YtbChannelID
import cats.Monad
import cats.MonadThrow
import cats.Applicative
import cats.Functor
import com.jb.domain.ChannelMeta
import com.jb.domain.UnixTS
import io.github.iltotore.iron.*
import com.jb.algebras.Algebras

final case class GetChannelInfo[F[_]: Async: MonadThrow: Functor](
    yqa: YoutubeQueryAlg[F],
) {
  def channelInfo(id: YtbChannelID): F[ChannelInfo] = {
    val ch = yqa.channelInfo(id)
    Functor[F].map(ch)(ChannelInfo(_, ChannelMeta(UnixTS(1), None)))
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
