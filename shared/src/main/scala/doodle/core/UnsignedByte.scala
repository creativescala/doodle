package doodle.core

final case class UnsignedByte(value: Byte) extends AnyVal {
  def +(that: UnsignedByte): Int =
    this.value + that.value

  def -(that: UnsignedByte): Int =
    this.value - that.value

  def get: Int =
    (value + 128)

  def toNormalized: Normalized =
    Normalized.clip(get.toDouble / UnsignedByte.MaxValue.get.toDouble)

  def toCanvas: String =
    (value + 128).toString
}
object UnsignedByte {
  val MinValue = UnsignedByte(-128.toByte)
  val MaxValue = UnsignedByte(127.toByte)

  def clip(value: Int): UnsignedByte =
    value match {
      case v if value < 0 => MinValue
      case v if value > 255 => MaxValue
      case v => UnsignedByte((v - 128).toByte)
    }
}
