package com.jb.routes

import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

val v1: PublicEndpoint[Unit, Unit, Unit, Any] = endpoint.in("v1.0")
