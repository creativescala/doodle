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
  def toTurns: Normalised =
    Normalised.clip(this.get / Angle.TwoPi)

  def toDegrees: Double =
    (this.get / Angle.TwoPi) * 360

  def toCanvas: String =
    this.toDegrees.toString
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
  def turns(t: Double): Angle =
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

  def max(that: Normalised): Normalised =
    if(this.get > that.get)
      this
    else
      that

  def min(that: Normalised): Normalised =
    if(this.get < that.get)
      this
    else
      that

  def toPercentage: String =
    s"${get * 100}%"

  def toUnsignedByte: UnsignedByte =
    UnsignedByte.clip(Math.round(get * UnsignedByte.MaxValue.get).toInt)

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

  def get: Int =
    (value + 128)

  def toNormalised: Normalised =
    Normalised.clip(get.toDouble / UnsignedByte.MaxValue.get.toDouble)

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
