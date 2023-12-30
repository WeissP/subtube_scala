package com.jb.migrate

import fly4s.core.*
import fly4s.core.data.*
import cats.effect.*
import cats.syntax.all.*
import fly4s.implicits.*
import com.jb.config.*
import com.jb.config.AppEnvironment

def migrationDir(dbConfig: DatabaseConfig, env: AppEnvConfig): List[Location] = {
  val base = Location("filesystem:" ++ dbConfig.migrationDir)
  (env.appEnv, dbConfig.migrationMockDir) match {
    case (AppEnvironment.Production, _) => List(base)
    case (_, None)                      => List(base)
    case (_, Some(mockDir)) => List(base, Location("filesystem:" ++ mockDir))
  }
}

def migrateDb(
    dbConfig: DatabaseConfig,
    env: AppEnvConfig,
): Resource[IO, MigrateResult] = {
  Fly4s
    .make[IO](
      url = dbConfig.url,
      user = dbConfig.user,
      password = dbConfig.password.map(_.value.toCharArray()),
      config = Fly4sConfig(
        table = dbConfig.migrationsTable,
        locations = migrationDir(dbConfig, env),
      ),
    )
    .evalMap(_.migrate)
}

object Main extends ResourceApp.Simple {
  def run: Resource[IO, Unit] = {
    for {
      dbCfg <- Resource.eval(databaseConfig.load[IO])
      envCfg <- Resource.eval(appEnvConfig.load[IO])
      _ <- migrateDb(dbCfg, envCfg)
    } yield ()
  }
}
