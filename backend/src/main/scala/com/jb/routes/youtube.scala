package com.jb.routes.youtube

import cats.effect.IO
import cats.effect.kernel.Async
import com.jb.domain.*
import com.jb.domain.schemas.given
import com.jb.programs.Programs
import com.jb.routes.{queryParam, v1}
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.pickler.*
import sttp.tapir.server.ServerEndpoint

private val youtube = v1.in("youtube")

private object tag {
  val base = youtube.in("tag").in(path[MediaTag]("tag"))
  val videos = base
    .in("videos")
    .in(queryParam.cacheRefreshThreshold)
    .in(queryParam.pagination)
    .in(queryParam.allowedMediaSources)
    .in(queryParam.allowedTaggingMethods)
    .out(jsonBody[List[MediaInfo[YoutubeMedia]]])
    .description("Return videos of the given tag")

  val channels = base
    .in("channels")
    .in(queryParam.cacheRefreshThreshold)
    .in(queryParam.pagination)
    .out(jsonBody[List[ChannelInfo]])
    .description("Return channels of the given tag")
}

private object channel {
  val base = youtube.in("channel")
  val withID = base.in(path[YtbChannelID]("channel_id"))

  val info = withID.out(jsonBody[ChannelInfo])

  val videos = withID
    .in("videos")
    .in(queryParam.mediaSortOrder)
    .in(queryParam.cacheRefreshThreshold)
    .in(queryParam.pagination)
    .out(jsonBody[List[MediaInfo[YoutubeMedia]]])
    .description("Return videos of the given channel")

  val search = base
    .in("search")
    .in(query[String]("query"))
    .out(jsonBody[List[ChannelInfo]])
    .description("Return a list of ChannelInfo with respect to the given query")
}

def endpoints(programs: Programs[IO]): List[ServerEndpoint[Any, IO]] = List(
  tag.videos.serverLogicSuccess(programs.fetchCacheVideos.videosByTag),
  tag.channels.serverLogicSuccess(_ => ???),
  channel.info.serverLogicSuccess(programs.getChannelInfo.channelInfo),
  channel.videos.serverLogicSuccess(programs.fetchCacheVideos.videosByChannel),
  channel.search.serverLogicSuccess(_ => ???),
)
