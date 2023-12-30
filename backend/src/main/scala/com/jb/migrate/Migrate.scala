package com.jb.migrate

import fly4s.core.*
import fly4s.core.data.*
import cats.effect.*
import cats.syntax.all.*
import fly4s.implicits.*
import com.jb.config.DatabaseConfig
import com.jb.config.databaseConfig

def migrateDb(dbConfig: DatabaseConfig): Resource[IO, MigrateResult] = {
  Fly4s
    .make[IO](
      url = dbConfig.url,
      user = dbConfig.user,
      password = dbConfig.password.map(_.value.toCharArray()),
      config = Fly4sConfig(
        table = dbConfig.migrationsTable,
        locations = Locations("filesystem:" ++ dbConfig.migrationsLocation),
      ),
    )
    .evalMap(_.migrate)
}

object Main extends ResourceApp.Simple {
  def run: Resource[IO, Unit] = {
    for {
      c <- Resource.eval(databaseConfig.load[IO])
      _ <- migrateDb(c)
    } yield ()
  }
}
