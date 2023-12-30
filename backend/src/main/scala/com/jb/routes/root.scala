package com.jb.routes

import cats.effect.IO.asyncForIO
import cats.effect.*
import cats.syntax.all.*
import com.jb.config.*
import com.jb.algebras.Algebras
import com.jb.config.AppConfig
import com.jb.domain.*
import com.jb.domain.schemas.given
import com.jb.programs.Programs
import com.jb.routes.youtube.tag
import io.github.iltotore.iron.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.CORS
import org.http4s.server.{Router, Server}
import sttp.tapir.Schema.annotations.description
import sttp.tapir.*
import sttp.tapir.files.*
import sttp.tapir.generic.Configuration
import sttp.tapir.generic.auto.*
import sttp.tapir.json.pickler.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.*
import sttp.tapir.server.model.ValuedEndpointOutput
import sttp.tapir.swagger.bundle.SwaggerInterpreter

def myFailureResponse(m: String): ValuedEndpointOutput[_] =
  ValuedEndpointOutput(jsonBody[MyFailure], MyFailure(m))

val serverOptions: Http4sServerOptions[IO] = {
  Http4sServerOptions.customiseInterceptors
    .defaultHandlers(myFailureResponse)
    .options
}

object queryParam {
  val mediaSortOrder = {
    query[MediaSortOrder]("sort_order")
      // .description(extraDesc("The sort order of medias. "))
      .default(MediaSortOrder.Newest)
  }

  val cacheRefreshThreshold = query[UnixTS]("cache_refresh_threshold")
    .default(UnixTS(1))
    .description(
      "A UNIX timestamp that sets the oldest allowed cache. If the cached information is (strictly) older than this threshold, it will be updated and then returned; otherwise, it can be directly returned.",
    )

  val allowedMediaSources = query[List[MediaSource]]("allowed_media_sources")
    .default(MediaSource.values.toList)
  val allowedTaggingMethods = query[List[TaggingMethod]]("allowed_tagging_methods")
    .default(TaggingMethod.values.toList)

  val pagination = EndpointInput.derived[Pagination]
}

sealed abstract class Endpoints private (
    c: ServerConfig,
    programs: Programs[IO],
) {
  val apiEndpoints = youtube.endpoints(programs)

  val docEndpoints = {
    SwaggerInterpreter()
      .fromServerEndpoints[IO](apiEndpoints, "subtube", "1.0.0")
  }

  val frontend = staticFilesGetServerEndpoint[IO](emptyInput)(c.frontend)

  val all = apiEndpoints ++ docEndpoints :+ frontend
}

object Endpoints {
  def make(c: ServerConfig, programs: Programs[IO]) = new Endpoints(c, programs) {}
}

def httpServer(
    c: AppConfig,
    endpoints: Endpoints,
): Resource[IO, Server] = {
  val appRoutes =
    Http4sServerInterpreter[IO](serverOptions).toRoutes(endpoints.all)

  val routes = c.env.appEnv match {
    case AppEnvironment.Local      => CORS.policy.withAllowOriginAll(appRoutes)
    case AppEnvironment.Production => appRoutes
  }

  EmberServerBuilder
    .default[IO]
    .withHost(c.server.host)
    .withPort(c.server.port)
    .withHttpApp(Router("/" -> routes).orNotFound)
    .build
}
