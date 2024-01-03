package com.jb.migrate

import cats.effect.*
import cats.syntax.all.*
import com.jb.config.*
import fly4s.core.*
import fly4s.core.data.*
import fly4s.implicits.*
import enumeratum.*
import com.jb.migrate.MigrateCommand.Clean
import com.jb.migrate.MigrateCommand.Migrate

def migrationDir(
    dbConfig: DatabaseConfig,
    env: Option[AppEnvConfig] = None,
): List[Location] = {
  val base = Location("filesystem:" ++ dbConfig.migrationDir)
  (env.map(_.appEnv), dbConfig.migrationMockDir) match {
    case (Some(AppEnvironment.Production), _) => List(base)
    case (_, None)                            => List(base)
    case (_, Some(mockDir)) => List(base, Location("filesystem:" ++ mockDir))
  }
}

def migrator(
    dbConfig: DatabaseConfig,
    env: Option[AppEnvConfig] = None,
): Resource[IO, Fly4s[IO]] = {
  Fly4s
    .make[IO](
      url = dbConfig.jdbcUrl,
      user = Some(dbConfig.user),
      password = dbConfig.password.map(_.value.toCharArray()),
      config = Fly4sConfig(
        table = dbConfig.migrationsTable,
        locations = migrationDir(dbConfig, env),
        cleanDisabled = false,
      ),
    )
}

sealed trait MigrateCommand extends EnumEntry
object MigrateCommand extends Enum[MigrateCommand] {
  case object Clean extends MigrateCommand
  case object Migrate extends MigrateCommand
  val values = findValues
}

def runMigrate(
    command: MigrateCommand,
    dbConfig: DatabaseConfig,
    env: Option[AppEnvConfig] = None,
) = {
  for {
    dbCfg <- Resource.eval(databaseConfig.load[IO])
    envCfg <- Resource.eval(appEnvConfig.load[IO])
    m <- migrator(dbCfg, Some(envCfg))
    _ <- Resource.eval(command match {
      case Clean   => m.clean
      case Migrate => m.migrate
    })
  } yield ()
}

object Main extends ResourceApp {
  def run(args: List[String]): Resource[IO, ExitCode] = {
    val command = args.headOption
      .flatMap(MigrateCommand.withNameInsensitiveOption)
      .getOrElse(MigrateCommand.Migrate)
    println(s"Running migrate command [${command}]...")
    for {
      dbCfg <- Resource.eval(databaseConfig.load[IO])
      envCfg <- Resource.eval(appEnvConfig.load[IO])
      _ <- runMigrate(command, dbCfg, Some(envCfg))
    } yield ExitCode.Success
  }
}
