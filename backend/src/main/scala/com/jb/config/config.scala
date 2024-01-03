package com.jb.config

import io.github.iltotore.iron.autoRefine
import cats.data.NonEmptyList
import cats.effect.kernel.Async
import cats.syntax.all.*
import ciris.http4s.given
import ciris.{http4s as _, *}
import com.comcast.ip4s.{Host, Port, port}
import enumeratum.{CirisEnum, Enum, EnumEntry}
import io.github.iltotore.iron.ciris.given
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.constraint.string.StartWith
import io.github.iltotore.iron.given
import io.github.iltotore.iron.{:|, RefinedTypeOps, refine}
import org.http4s.Uri
import org.http4s.syntax.all.uri
import org.log4s.LogLevel
import com.jb.config.AppEnvironment.Local
import com.jb.config.AppEnvironment.Testing
import com.jb.config.AppEnvironment.Production

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
    user: String,
    host: String,
    port: Int,
    dbName: String,
    password: Option[Secret[String]],
    migrationsTable: String,
    migrationDir: String :| Not[Empty],
    migrationMockDir: Option[String :| Not[Empty]],
) {
  val jdbcUrl: JDBCUrl = s"jdbc:postgresql://${host}:${port}/${dbName}".refine
}

val databaseConfig: ConfigLoader[DatabaseConfig] = {
  (
    env("PGUSER").as[String].default("postgres"),
    env("PGHOST").as[String],
    env("PGPORT").as[Int],
    env("DBNAME").as[String],
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
