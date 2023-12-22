package com.jb.domain

import com.jb.domain.integration.pickle.given
import com.jb.domain.integration.upickle.given
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.codec.iron.TapirCodecIron
import sttp.tapir.json.pickler.{Pickler, PicklerConfiguration}
import sttp.tapir.{Codec, FieldName, Schema, SchemaType}

object schemas extends TapirCodecIron {
  import com.jb.domain.given

  given (using s: Schema[MediaTag]): Schema[MediaTag] =
    s.description("The tag of a group of medias")

  given (using s: Schema[YtbVideoID]): Schema[YtbVideoID] = {
    s.description("The youtube video ID (must be 11 ASCII characters)")
      .encodedExample("lOwjw1Ja83Y")
  }

  given (using s: Schema[YtbChannelID]): Schema[YtbChannelID] = {
    s.description("The youtube channel ID (must be 24 ASCII characters)")
      .encodedExample("UCjuNibFJ21MiSNpu8LZyV4w")
  }

  given (using s: Schema[MediaLength]): Schema[MediaLength] = {
    s.description("The length of the media in seconds")
  }

  given (using s: Schema[UnixTS]): Schema[UnixTS] = {
    s.description("UNIX Timestamp")
  }

  given (using s: Schema[CachedAt]): Schema[CachedAt] = {
    s.description("The last cached UNIX Timestamp")
  }

  given PlainCodec[MediaSortOrder] = {
    Codec
      .derivedEnumeration[String, MediaSortOrder]
      .defaultStringBased
      .schema(old => old.description("The sort order of medias"))
  }

  given PlainCodec[TaggingMethod] =
    Codec.derivedEnumeration[String, TaggingMethod].defaultStringBased

  given PlainCodec[MediaSource] =
    Codec.derivedEnumeration[String, MediaSource].defaultStringBased

  given customConfiguration: PicklerConfiguration =
    PicklerConfiguration.default.withSnakeCaseMemberNames

  given Pickler[UnixTS] = Pickler.derived
  given Pickler[CachedAt] = Pickler.derived
  given Pickler[MediaTag] = Pickler.derived
  given Pickler[MediaLength] = Pickler.derived
  given Pickler[MediaID] = Pickler.derived
  given Pickler[MediaTagID] = Pickler.derived
  given Pickler[MediaSource] = Pickler.derived
  given Pickler[TaggingMethod] = Pickler.derived
  given Pickler[MediaSortOrder] = Pickler.derived
  given Pickler[MediaMeta] = Pickler.derived
  given [M <: Media: Pickler]: Pickler[MediaInfo[M]] = Pickler.derived

  given Pickler[YtbChannelID] = Pickler.derived
  given Pickler[YtbVideoID] = Pickler.derived
  given Pickler[Thumbnail] = Pickler.derived
  given Pickler[Channel] = Pickler.derived
  given Pickler[ChannelMeta] = Pickler.derived
  given Pickler[ChannelInfo] = Pickler.derived
  given Pickler[YoutubeMedia] = Pickler.derived

  given Pickler[MyFailure] = Pickler.derived
}
