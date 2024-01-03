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

opaque type MediaTag = NonEmptyString
object MediaTag
    extends RefinedTypeOps[String, Not[Empty], MediaTag]
    with TapirCodecIron {
  given (using s: Schema[MediaTag]): Schema[MediaTag] =
    s.description("The tag of a group of medias")
  given Pickler[MediaTag] = Pickler.derived
}

type MediaTagID = UUID

case class TagEntry(id: MediaTagID, name: MediaTag, introduction: Option[String])
case class TagPatch(introduction: Option[String])
case class TagMediaEntry(tagID: MediaTagID, mediaID: MediaID, method: TaggingMethod)

sealed trait TaggingMethod extends EnumEntry with Snakecase
object TaggingMethod extends Enum[TaggingMethod] {
  case object YoutubeVideo extends TaggingMethod
  case object YoutubeChannel extends TaggingMethod
  val values = findValues

  given PlainCodec[TaggingMethod] =
    TapirCodec.derivedEnumeration[String, TaggingMethod].defaultStringBased
  given Pickler[TaggingMethod] = Pickler.derived
}

@description("The meta information regarding tag")
case class TagMeta(
    taggingMethods: List[TaggingMethod],
)
object TagMeta extends TapirCodecIron {
  given Pickler[TagMeta] = Pickler.derived
}

case class MediaInfoWithTag[M <: Media](info: MediaInfo[M], tagMeta: TagMeta)
object MediaInfoWithTag extends TapirCodecIron {
  given [M <: Media: Pickler]: Pickler[MediaInfoWithTag[M]] = Pickler.derived
}
