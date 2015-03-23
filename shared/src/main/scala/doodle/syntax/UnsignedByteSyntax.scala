package doodle.syntax

import doodle.core.UnsignedByte
import scala.language.implicitConversions

class ToUnsignedByteOps(val value: Int) extends AnyVal {
  def uByte: UnsignedByte =
    UnsignedByte.clip(value)
}

trait UnsignedByteSyntax {
  implicit def doubleToUnsignedByteOps(number: Int): ToUnsignedByteOps =
    new ToUnsignedByteOps(number)
}
