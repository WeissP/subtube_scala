package com.jb

import cats.effect.kernel.Resource
import cats.effect.std.{Console, Supervisor}
import cats.effect.{ExitCode, IO, IOApp, ResourceApp}
import com.comcast.ip4s.{Host, Port, port}
import com.jb.algebras.Algebras
import com.jb.config.appConfig
import com.jb.migrate.runMigrate
import com.jb.programs.Programs
import com.jb.resources.AppResources
import com.jb.routes
import com.jb.routes.{Endpoints, httpServer, serverOptions}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import sttp.tapir.server.http4s.Http4sServerInterpreter
import org.typelevel.otel4s.trace.Tracer
import com.jb.env.given
import com.jb.migrate.MigrateCommand

package env {
  implicit def logger: Logger[IO] = Slf4jLogger.getLogger[IO]
  implicit val tracer: Tracer[IO] = Tracer.noop[IO]
}

object Main extends ResourceApp.Forever {
  def run(args: List[String]): Resource[IO, Unit] = {
    for {
      c <- Resource.eval(appConfig.load[IO])
      _ <- runMigrate(MigrateCommand.Migrate, c.db, Some(c.env))
      r <- AppResources.make(c)
      algebras <- Algebras.make[IO](r.client, r.dbConnections, c)
      programs = Programs.make[IO](algebras)
      endpoints = Endpoints.make(c.server, programs)
      _ <- httpServer(c, endpoints)
    } yield ()
  }
}
