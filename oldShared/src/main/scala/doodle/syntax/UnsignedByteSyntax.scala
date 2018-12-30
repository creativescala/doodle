package doodle.syntax

import doodle.core.UnsignedByte

trait UnsignedByteSyntax {
  implicit class ToUnsignedByteOps(val value: Int) {
    def uByte: UnsignedByte =
      UnsignedByte.clip(value)
  }
}
