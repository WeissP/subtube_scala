package com.jb.resources

import cats.effect.kernel.{Async, Resource}
import com.jb.config.AppConfig
import org.http4s.client.Client
import org.http4s.client.middleware.FollowRedirect
import org.http4s.ember.client.EmberClientBuilder

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
