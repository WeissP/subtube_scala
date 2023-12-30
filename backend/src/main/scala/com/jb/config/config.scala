package com.jb.config

import cats.data.NonEmptyList
import cats.effect.kernel.Async
import cats.syntax.all.*
import ciris.{http4s as _, *}
import ciris.http4s.given
import com.comcast.ip4s.{Host, Port, port}
import io.github.iltotore.iron.ciris.given
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.constraint.string.StartWith
import io.github.iltotore.iron.{cats as _, ciris as _, *}
import org.http4s.Uri
import org.http4s.syntax.all.uri
import enumeratum.{CirisEnum, Enum, EnumEntry}
import org.log4s.LogLevel

sealed trait AppEnvironment extends EnumEntry

object AppEnvironment extends Enum[AppEnvironment] with CirisEnum[AppEnvironment] {
  case object Local extends AppEnvironment
  case object Testing extends AppEnvironment
  case object Production extends AppEnvironment

  val values = findValues
}

type ConfigLoader[T] = ConfigValue[Effect, T]
type JDBCUrl = String :| StartWith["jdbc:"]

final case class DatabaseConfig(
    url: JDBCUrl,
    user: Option[String],
    password: Option[Secret[String]],
    migrationsTable: String,
    migrationDir: String :| Not[Empty],
    migrationMockDir: Option[String :| Not[Empty]],
)

val databaseConfig: ConfigLoader[DatabaseConfig] = {
  (
    env("ST_DATABASE_JDBC_URL").as[JDBCUrl],
    env("PGUSER").as[String].option,
    env("PGPASSWORD").as[String].secret.option,
    env("ST_DATABASE_MIGRATIONS_TABLE").as[String].default("flyway"),
    env("ST_DATABASE_MIGRATION_DIR").as[String :| Not[Empty]],
    env("ST_DATABASE_MIGRATION_MOCK_DIR").as[String :| Not[Empty]].option,
  ).parMapN(DatabaseConfig.apply)
}

final case class YoutubeQueryConfig(
    invidiousRoot: Uri,
    invidiousRootFallback: Option[Uri],
)

val youtubeQueryConfigConfig: ConfigLoader[YoutubeQueryConfig] = {
  (
    env("ST_INVIDIOUS_ROOT_URL").as[Uri],
    env("ST_INVIDIOUS_ROOT_URL_FALLBACK").as[Uri].option,
  ).parMapN(YoutubeQueryConfig.apply)
}

final case class AppEnvConfig(appEnv: AppEnvironment, logLevel: String)
val appEnvConfig: ConfigLoader[AppEnvConfig] = {
  (
    env("ST_APP_ENV").as[AppEnvironment].default(AppEnvironment.Local),
    env("ST_APP_LOG_LEVEL").as[String].default("Debug"),
  ).parMapN(AppEnvConfig.apply)
}

final case class ServerConfig(
    host: Host,
    port: Port,
    frontend: String,
)
val serverConfig: ConfigLoader[ServerConfig] = {
  (
    env("ST_SERVER_HOST").as[Host].default(Host.fromString("localhost").get),
    env("ST_SERVER_PORT").as[Port],
    env("ST_FRONTEND").as[String],
  ).parMapN(ServerConfig.apply)
}

final case class AppConfig(
    env: AppEnvConfig,
    server: ServerConfig,
    db: DatabaseConfig,
    ytb: YoutubeQueryConfig,
)

val appConfig: ConfigLoader[AppConfig] = {
  (
    appEnvConfig,
    serverConfig,
    databaseConfig,
    youtubeQueryConfigConfig,
  ).parMapN(AppConfig.apply)
}
