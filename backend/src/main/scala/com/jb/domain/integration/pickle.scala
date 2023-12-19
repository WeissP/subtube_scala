package com.jb.domain.integration

import _root_.upickle.default.{Reader as UpickleReader, Writer as UpickleWriter}
import io.github.iltotore.iron.IronType
import sttp.tapir.Schema
import sttp.tapir.json.pickler.{Pickler, TapirPickle}

import scala.compiletime.summonInline

/** To create Pickles for Iron types
  */
object pickle {

  given [A, C](using
      schemaEv: Schema[IronType[A, C]],
      readerEv: UpickleReader[IronType[A, C]],
      writerEv: UpickleWriter[IronType[A, C]],
  ): Pickler[IronType[A, C]] = Pickler(
    new TapirPickle[IronType[A, C]] {
      override lazy val reader =
        readerEv.asInstanceOf[Reader[IronType[A, C]]]
      override lazy val writer =
        writerEv.asInstanceOf[Writer[IronType[A, C]]]
    },
    schemaEv,
  )
//  given[A,C](using ev: ClassTag[A]):ClassTag[IronType[A, C]]  = ev.asInstanceOf[ClassTag[IronType[A, C]]]
//  given[A,C]: Pickler[IronType[A, C]] = Pickler.derived[IronType[A, C]]
  given [A, C](using picker: Pickler[IronType[A, C]]): Pickler[Option[IronType[A, C]]] =
    picker.asOption
}
