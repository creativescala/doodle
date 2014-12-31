package doodle

import scala.annotation.tailrec

/**
  * An angle in radians, normalised to be in [0, 2pi)
  */
final case class Angle(get: Double) extends AnyVal {
  def +(that: Angle): Angle =
    Angle.radians(this.get + that.get)

  def -(that: Angle): Angle =
    Angle.radians(this.get - that.get)

  /** Angle as the proportion of a full turn around a circle */
  def toTurn: Normalised =
    Normalised.clip(this.get / Angle.TwoPi)

  def toDegrees: Double =
    this.get * Angle.TwoPi
}
object Angle {
  val TwoPi = math.Pi * 2

  @tailrec
  def normalise(rad: Double): Double =
    rad match {
      case r if r < 0.0 =>
        normalise(r + TwoPi)
      case r if r > TwoPi =>
        normalise(r - TwoPi)
      case r => r
    }

  def degrees(deg: Double): Angle = 
    Angle(normalise(deg * TwoPi / 360.0))

  def radians(rad: Double): Angle =
    Angle(normalise(rad))

  /**
    *  A turn represents angle as a proportion of a full turn around a
    *  circle, with a full turn being 1.0
    */
  def turn(t: Double): Angle =
    Angle(normalise(t * TwoPi))
}

/**
  * A value in the range [0, 1]
  */
final case class Normalised(get: Double) extends AnyVal {
  def +(that: Normalised): Double =
    this.get + that.get

  def -(that: Normalised): Double =
    this.get - that.get

  def toPercentage: String =
    s"${get * 100}%"
  def toCanvas: String =
    get.toString
}
object Normalised {
  def clip(value: Double): Normalised =
    value match {
      case v if value < 0.0 => Normalised(0.0)
      case v if value > 1.0 => Normalised(1.0)
      case v => Normalised(v)
    }
}

final case class UnsignedByte(value: Byte) extends AnyVal {
  def +(that: UnsignedByte): Int =
    this.value + that.value

  def -(that: UnsignedByte): Int =
    this.value - that.value

  def toCanvas: String =
    (value + 128).toString
  def get: Int =
    (value + 128)
}
object UnsignedByte {
  def clip(value: Int): UnsignedByte =
    value match {
      case v if value < 0 => UnsignedByte(-128.toByte)
      case v if value > 255 => UnsignedByte(127.toByte)
      case v => UnsignedByte((v - 128).toByte)
    }
}
