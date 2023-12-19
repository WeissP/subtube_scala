package com.jb.routes.youtube

import cats.effect.IO
import com.jb.domain.*
import com.jb.domain.schemas.given
import com.jb.routes.{v1}
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.pickler.*
import sttp.tapir.server.ServerEndpoint
import com.jb.routes.queryParam
import com.jb.programs.Programs
import cats.effect.kernel.Async

private val base = v1.in("youtube")

val tag = {
  base
    .in("tag")
    .in(path[MediaTag]("tag"))
    .in(queryParam.mediaSortOrder)
    .in(queryParam.cacheRefreshThreshold)
    .in(queryParam.pagination)
    .in(queryParam.allowedMediaSources)
    .in(queryParam.allowedTaggingMethods)
    .out(jsonBody[List[MediaInfo[YoutubeMedia]]])
    .description("Return videos of given tag")
}

private val channel = {
  base
    .in("channel")
    .in(path[YtbChannelID]("channel_id"))
    .out(jsonBody[ChannelInfo])
}

private val searchChannel = {
  base
    .in("search" / "channel")
    .in(query[String]("query"))
    .out(jsonBody[List[ChannelInfo]])
    .description("Return a list of ChannelInfo with respect to the given query")
}

def endpoints(programs: Programs[IO]): List[ServerEndpoint[Any, IO]] = List(
  tag.serverLogicSuccess(_ => ???),
  channel.serverLogicSuccess(programs.getChannelInfo.channelInfo),
  searchChannel.serverLogicSuccess(_ => ???),
)
