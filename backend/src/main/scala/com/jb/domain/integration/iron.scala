package com.jb.domain.integration

import sttp.tapir.Codec.JsonCodec
import sttp.tapir.CodecFormat.Json
import sttp.tapir.DecodeResult.Error.JsonDecodeException
import sttp.tapir.json.pickler.Pickler
import sttp.tapir.{
  DecodeResult,
  EndpointIO,
  Schema,
  ValidationError,
  stringBodyUtf8AnyFormat,
}

object iron {

  case class IronException(originalValue: Any, errorMessage: String) extends Exception

  def ironJsonBody[T: Pickler]: EndpointIO.Body[String, T] = stringBodyUtf8AnyFormat(
    new IronCodec(summon[Pickler[T]].toCodec),
  )

  /** Custom codec for body containing iron-constrainted types. This is necessary to
    * change decoding `Error` into `InvalidValue` based on custom exception
    *
    * @param delegate
    *   delegate `Codec` for the same type `T` throwing `Error` with wrapped custom
    *   exception
    */
  class IronCodec[T](delegate: JsonCodec[T]) extends JsonCodec[T] {
    override def rawDecode(l: String): DecodeResult[T] = delegate.rawDecode(l) match {
      case DecodeResult.Error(
            _,
            JsonDecodeException(_, IronException(originalValue, errorMessage)),
          ) =>
        DecodeResult.InvalidValue(
          List(ValidationError(null, originalValue, List(), Some(errorMessage))),
        )
      case other =>
        other
    }

    override def encode(h: T): String = delegate.encode(h)

    override def schema: Schema[T] = delegate.schema

    override def format: Json = delegate.format
  }

}
