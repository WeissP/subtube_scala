package com.jb.resources

import com.jb.config.AppConfig
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import cats.effect.kernel.{Async}
import cats.effect.kernel.Resource
import org.http4s.client.middleware.FollowRedirect

sealed abstract class AppResources[F[_]](
    val client: Client[F],
)

object AppResources {
  def make[F[_]: Async](cfg: AppConfig): Resource[F, AppResources[F]] = {
    EmberClientBuilder
      .default[F]
      // .withTimeout(c.timeout)
      // .withIdleTimeInPool(c.idleTimeInPool)
      .build
      .map(c => new AppResources[F](FollowRedirect(10, _ => true)(c)) {})
  }
}
