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

type NonEmptyString = String :| Not[Empty]

type UnsignedInt = Int :| GreaterEqual[0]
object UnsignedInt extends RefinedTypeOps[Int, GreaterEqual[0], UnsignedInt]

type PositiveInt = Int :| Positive
object PositiveInt
    extends RefinedTypeOps[Int, Positive, PositiveInt]
    with TapirCodecIron {
  given Pickler[PositiveInt] = Pickler.derived
}

opaque type UnixTS = Long :| Positive
object UnixTS extends RefinedTypeOps[Long, Positive, UnixTS] with TapirCodecIron {
  given (using s: Schema[UnixTS]): Schema[UnixTS] = {
    s.description("UNIX Timestamp")
  }
  given Pickler[UnixTS] = Pickler.derived
}

opaque type CachedAt = Long :| Positive
object CachedAt extends RefinedTypeOps[Long, Positive, CachedAt] with TapirCodecIron {
  def now[F[_]: Functor](using c: Clock[F]): F[CachedAt] =
    c.realTime.map(_.toSeconds.refine)

  given skunk: Codec[CachedAt] = int8.refined[Positive]
  given (using s: Schema[CachedAt]): Schema[CachedAt] = {
    s.description("The last cached UNIX Timestamp")
  }
  given Pickler[CachedAt] = Pickler.derived
}
extension (c: CachedAt) {
  def toDateTime: LocalDateTime = {
    LocalDateTime.ofEpochSecond(c, 0, ZoneOffset.UTC)
  }
}

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

case class HttpErrMsg(msg: String)
object HttpErrMsg extends TapirCodecIron {
  given Pickler[HttpErrMsg] = Pickler.derived
}
