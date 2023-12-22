package com.jb.algebras

import cats.effect.kernel.Async
import com.jb.algebras.youtube.Invidious
import com.jb.config.AppConfig
import com.jb.resources.AppResources
import org.http4s.client.Client

sealed abstract class Algebras[F[_]] private (
    val youtubeQueryAlg: YoutubeQueryAlg[F],
)

object Algebras {
  def make[F[_]: Async](c: Client[F], config: AppConfig) = {

    new Algebras[F](
      youtubeQueryAlg = Invidious.client[F](c, config.invidiousRoot),
    ) {}
  }
}
