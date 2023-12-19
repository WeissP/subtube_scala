package com.jb

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{Host, Port, port}
import com.jb.routes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerInterpreter
import com.jb.routes.serverOptions
import com.jb.config.AppConfig
import cats.effect.std.Supervisor
import com.jb.resources.AppResources
import cats.effect.ResourceApp
import cats.effect.kernel.Resource
import com.jb.routes.httpServer
import com.jb.algebras.Algebras
import com.jb.programs.Programs
import com.jb.routes.Endpoints

object Main extends ResourceApp.Forever {
  def run(args: List[String]): Resource[IO, Unit] = {
    for {
      c <- Resource.eval(AppConfig.load[IO])
      r <- AppResources.make[IO](c)
      algebras = Algebras.make[IO](r.client, c)
      programs = Programs.make[IO](algebras)
      endpoints = Endpoints.make(programs)
      _ <- httpServer(c, endpoints)
    } yield ()
  }
}
