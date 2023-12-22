package com.jb.domain

import io.github.iltotore.iron._
import io.github.iltotore.iron.constraint.all._
import io.github.iltotore.iron.constraint.any.DescribedAs
import sttp.tapir.Schema.annotations.description
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.pickler.*

opaque type YtbChannelID = String :| FixedLength[24]
object YtbChannelID extends RefinedTypeOps[String, FixedLength[24], YtbChannelID]

opaque type YtbVideoID = String :| FixedLength[11]
object YtbVideoID extends RefinedTypeOps[String, FixedLength[11], YtbVideoID]

opaque type Continuation = NonEmptyString
object Continuation extends RefinedTypeOps[String, Not[Empty], Continuation]

case class Thumbnail(
    height: Int :| Positive,
    width: Int :| Positive,
    url: String,
)

case class Channel(
    id: YtbChannelID,
    @description("Channel description in HTML form")
    descriptionHtml: String,
    @description("Number of subscribtions")
    subCount: Int :| Positive,
    @description("Number of videos")
    thumbnails: List[Thumbnail],
)

case class ChannelMeta(
    cachedAt: CachedAt,
    intro: Option[NonEmptyString],
)

case class ChannelInfo(channel: Channel, meta: ChannelMeta)

@description("The meta information of Youtube media")
case class YoutubeMedia(
    length: MediaLength,
    title: String,
    thumbnails: List[Thumbnail],
    channelID: YtbChannelID,
    videoID: YtbVideoID,
    published: UnixTS,
    @description("Media description in HTML form")
    descriptionHtml: String,
) extends Media(length, title, thumbnails)

case class YtbMediaUpdate()
case class YtbChannelUpdate()
