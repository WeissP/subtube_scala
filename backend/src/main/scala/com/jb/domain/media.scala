package com.jb.domain

import cats.Functor
import cats.effect.kernel.Clock
import cats.syntax.all.*
import com.jb.domain.integration.pickle.given
import com.jb.domain.integration.upickle.given
import enumeratum.EnumEntry.Snakecase
import enumeratum.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.constraint.any.DescribedAs
import io.github.iltotore.iron.given
import io.github.iltotore.iron.skunk.*
import io.github.iltotore.iron.skunk.given
import io.github.iltotore.iron.{:|, RefinedTypeOps, refine}
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.EndpointIO.annotations.query
import sttp.tapir.Schema.annotations.*
import sttp.tapir.codec.iron.*
import sttp.tapir.generic.Configuration
import sttp.tapir.json.pickler.Pickler
import sttp.tapir.{Codec as TapirCodec, FieldName, Schema, SchemaType}

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

opaque type MediaLength = Int :| Positive
object MediaLength
    extends RefinedTypeOps[Int, Positive, MediaLength]
    with TapirCodecIron {
  given (using s: Schema[MediaLength]): Schema[MediaLength] = {
    s.description("The length of the media in seconds")
  }
  given Pickler[MediaLength] = Pickler.derived
}

type MediaID = UUID

sealed trait MediaSource extends EnumEntry with Snakecase
object MediaSource extends Enum[MediaSource] {
  case object Local extends MediaSource
  case object Youtube extends MediaSource
  val values = findValues

  given PlainCodec[MediaSource] =
    TapirCodec.derivedEnumeration[String, MediaSource].defaultStringBased
  given Pickler[MediaSource] = Pickler.derived
}

sealed trait MediaSortOrder extends EnumEntry with Snakecase
object MediaSortOrder extends Enum[MediaSortOrder] {
  case object Newest extends MediaSortOrder
  case object Popular extends MediaSortOrder
  val values = findValues

  given PlainCodec[MediaSortOrder] = {
    TapirCodec
      .derivedEnumeration[String, MediaSortOrder]
      .defaultStringBased
      .schema(old => old.description("The sort order of medias"))
  }
  given Pickler[MediaSortOrder] = Pickler.derived
}

class Media(
    length: MediaLength,
    title: String,
    thumbnails: List[Thumbnail],
)

@description("The meta information of media")
case class MediaMeta(
    mediaID: MediaID,
    @description("The source of the media")
    source: MediaSource,
    @description("The UNIX timestamp that the media info was cached")
    cachedAt: CachedAt,
    @description("Personal introduction of the media")
    introduction: Option[NonEmptyString],
)
object MediaMeta extends TapirCodecIron {
  given Pickler[MediaMeta] = Pickler.derived
}

case class MediaInfo[M <: Media](
    @description("The information of the media")
    media: M,
    @description(
      "The meta information of the media, normally such information doesn't come from the media source, but from the backend server",
    )
    meta: MediaMeta,
)
object MediaInfo extends TapirCodecIron {
  given [M <: Media: Pickler]: Pickler[MediaInfo[M]] = Pickler.derived
}
