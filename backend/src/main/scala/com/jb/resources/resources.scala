package com.jb.resources

import cats.effect.*
import cats.syntax.all.*
import cats.effect.kernel.{Async, Resource}
import com.jb.config.AppConfig
import org.http4s.client.Client
import org.http4s.client.middleware.FollowRedirect
import org.http4s.ember.client.EmberClientBuilder
import skunk.Session
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*
import com.jb.env.given
import org.typelevel.log4cats.Logger
import fs2.io.net.Network
import com.jb.config.DatabaseConfig

type DbPool[F[_]] = Resource[F, Session[F]]

final case class AppResources(
    val client: Client[IO],
    val dbConnections: DbPool[IO],
)

extension (cfg: DatabaseConfig) {
  def initPool: Resource[IO, DbPool[IO]] = {
    Session.pooled[IO](
      host = cfg.host,
      user = cfg.user,
      database = cfg.dbName,
      port = cfg.port,
      password = cfg.password.map(_.value),
      max = 10,
    )
  }
}

val client: Resource[IO, Client[IO]] = EmberClientBuilder
  .default[IO]
  // .withTimeout(c.timeout)
  // .withIdleTimeInPool(c.idleTimeInPool)
  .build
  .map(FollowRedirect(10, _ => true))

object AppResources {
  def make(cfg: AppConfig): Resource[IO, AppResources] = {
    (client, cfg.db.initPool).parMapN(AppResources.apply)
  }
}
