package com.jb.substube

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  val run = SubstubeServer.run[IO]
