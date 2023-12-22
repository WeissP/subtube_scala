package com.jb.config

import cats.effect.kernel.Async
import cats.syntax.all.*
import com.comcast.ip4s.{Host, Port, port}
import org.http4s.Uri
import org.http4s.syntax.all.uri

case class AppConfig(invidiousRoot: Uri, host: Host, port: Port)

object AppConfig {
  def load[F[_]: Async]: F[AppConfig] = {
    val port = sys.env
      .get("ST_SERVER_PORT")
      .flatMap(_.toIntOption)
      .flatMap(Port.fromInt)
      .getOrElse(port"9090")

    val host = Host.fromString("localhost").get
    AppConfig(
      invidiousRoot = uri"https://invidious.nerdvpn.de" / "api" / "v1",
      host,
      port,
    ).pure
  }
}
