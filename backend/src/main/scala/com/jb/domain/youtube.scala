package com.jb.domain

import com.jb.domain.integration.pickle.given
import com.jb.domain.integration.upickle.given
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.constraint.any.DescribedAs
import io.github.iltotore.iron.given
import io.github.iltotore.iron.skunk.*
import io.github.iltotore.iron.skunk.given
import io.github.iltotore.iron.{:|, RefinedTypeOps, refine}
import org.typelevel.twiddles.Iso
import skunk.{Codec as SkunkCodec, *}
import skunk.codec.all.*
import skunk.implicits.*
import sttp.tapir.Schema.annotations.description
import sttp.tapir.codec.iron.TapirCodecIron
import sttp.tapir.json.pickler.*
import sttp.tapir.{Codec as TapirCodec, FieldName, Schema, SchemaType}

import java.util.UUID
import java.time.LocalDateTime

opaque type YtbChannelID = String :| FixedLength[24]
object YtbChannelID
    extends RefinedTypeOps[String, FixedLength[24], YtbChannelID]
    with TapirCodecIron {
  given skunk: SkunkCodec[YtbChannelID] = text.refined[FixedLength[24]]
  given (using s: Schema[YtbChannelID]): Schema[YtbChannelID] = {
    s.description("The youtube channel ID (must be 24 ASCII characters)")
      .encodedExample("UCjuNibFJ21MiSNpu8LZyV4w")
  }
  given Pickler[YtbChannelID] = Pickler.derived
}

opaque type YtbVideoID = String :| FixedLength[11]
object YtbVideoID
    extends RefinedTypeOps[String, FixedLength[11], YtbVideoID]
    with TapirCodecIron {
  given (using s: Schema[YtbVideoID]): Schema[YtbVideoID] = {
    s.description("The youtube video ID (must be 11 ASCII characters)")
      .encodedExample("lOwjw1Ja83Y")
  }
  given Pickler[YtbVideoID] = Pickler.derived
}

opaque type Continuation = NonEmptyString
object Continuation extends RefinedTypeOps[String, Not[Empty], Continuation]

enum FetchState {
  case Init
  case Done
  case InProgress(cont: Continuation)
}

case class Thumbnail(
    height: PositiveInt,
    width: PositiveInt,
    url: String,
)
object Thumbnail extends TapirCodecIron {
  given Pickler[Thumbnail] = Pickler.derived
}

case class Channel(
    id: YtbChannelID,
    name: String,
    @description("Channel description in HTML form")
    descriptionHtml: String,
    @description("Number of subscribtions")
    subCount: Int :| Positive,
    @description("Number of videos")
    thumbnails: List[Thumbnail],
)
object Channel extends TapirCodecIron {
  given Pickler[Channel] = Pickler.derived
}

case class ChannelMeta(
    cachedAt: CachedAt,
    intro: Option[NonEmptyString],
)

object ChannelMeta extends TapirCodecIron {
  given Pickler[ChannelMeta] = Pickler.derived
}

case class ChannelInfo(channel: Channel, meta: ChannelMeta)
object ChannelInfo extends TapirCodecIron {
  given Pickler[ChannelInfo] = Pickler.derived
}

case class UUIDEntry[T](id: UUID, entry: T)
object UUIDEntry {
  given codec[T](using s: SkunkCodec[T]): SkunkCodec[UUIDEntry[T]] =
    (uuid *: s).to[UUIDEntry[T]]
}

case class YtbChannelEntry(
    ytbChannelId: YtbChannelID,
    channelName: String,
    description: String,
    subCount: Int :| Positive,
    introduction: Option[String],
    cachedAt: CachedAt,
)
object YtbChannelEntry {
  given codec: SkunkCodec[YtbChannelEntry] = {
    (YtbChannelID.skunk *: text *: text *: int4
      .refined[Positive] *: text.opt *: CachedAt.skunk)
      .to[YtbChannelEntry]
  }
}

@description("The information of Youtube media")
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
object YoutubeMedia {
  given Pickler[YoutubeMedia] = Pickler.derived
}
