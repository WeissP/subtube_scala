package com.jb.routes

import io.circe.generic.auto.*
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*

val v1: PublicEndpoint[Unit, Unit, Unit, Any] = endpoint.in("v1.0")
