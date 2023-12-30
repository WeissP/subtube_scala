package com.jb.domain

import cats.Functor
import cats.effect.kernel.Clock
import cats.syntax.all.*
import com.jb.domain.integration.pickle.given
import com.jb.domain.integration.upickle.given
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.constraint.any.DescribedAs
import sttp.tapir.EndpointIO.annotations.query
import sttp.tapir.Schema
import sttp.tapir.Schema.annotations.{
  default,
  description,
  encodedExample,
  encodedName,
  validate,
}
import sttp.tapir.codec.iron.*
import sttp.tapir.generic.Configuration
import sttp.tapir.json.pickler.Pickler

type UnsignedInt = Int :| GreaterEqual[0]
object UnsignedInt extends RefinedTypeOps[Int, GreaterEqual[0], UnsignedInt]

opaque type UnixTS = Long :| Positive
object UnixTS extends RefinedTypeOps[Long, Positive, UnixTS]

opaque type CachedAt = Long :| Positive
object CachedAt extends RefinedTypeOps[Long, Positive, CachedAt] {
  def now[F[_]: Functor](using c: Clock[F]): F[CachedAt] =
    c.realTime.map(_.toSeconds.refine)
}

type NonEmptyString = String :| Not[Empty]
opaque type MediaTag = NonEmptyString
object MediaTag extends RefinedTypeOps[String, Not[Empty], MediaTag]

opaque type MediaLength = Int :| Positive
object MediaLength extends RefinedTypeOps[Int, Positive, MediaLength]

opaque type MediaID = UnsignedInt
object MediaID extends RefinedTypeOps[Int, GreaterEqual[0], MediaID]

opaque type MediaTagID = UnsignedInt
object MediaTagID extends RefinedTypeOps[Int, GreaterEqual[0], MediaTagID]

enum MediaSource {
  case Local
  case Youtube
}

enum TaggingMethod {
  case SingleMedia
  case YoutubeChannel
}

enum MediaSortOrder {
  case Newest
  case Popular
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

case class MediaInfo[M <: Media](
    @description("The information of the media")
    media: M,
    @description(
      "The meta information of the media, normally such information doesn't come from the media source, but from the backend server",
    )
    meta: MediaMeta,
)

case class MediaUpdate()

case class Pagination(
    @query
    @default[UnsignedInt](0)
    @description(
      "The number of items to skip before starting to collect the result set",
    )
    offset: UnsignedInt,
    @query
    @description("Max number of returned items")
    @default[Int :| Positive](20)
    limit: Int :| Positive,
)

case class MyFailure(msg: String)
