package doodle.core

import scala.annotation.tailrec

/**
  * A value in the range [0, 1]
  */
final case class Normalized(get: Double) extends AnyVal {
  def +(that: Normalized): Double =
    this.get + that.get

  def -(that: Normalized): Double =
    this.get - that.get

  def max(that: Normalized): Normalized =
    if(this.get > that.get)
      this
    else
      that

  def min(that: Normalized): Normalized =
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

object Normalized {
  val MinValue = Normalized(0.0)
  val MaxValue = Normalized(1.0)

  def clip(value: Double): Normalized =
    value match {
      case v if value < 0.0 => MinValue
      case v if value > 1.0 => MaxValue
      case v => Normalized(v)
    }

  def wrap(value: Double): Normalized = {
    @tailrec def loop(value: Double): Normalized = value match {
      case v if v > 1.0 => loop(v - 1.0)
      case v if v < 0.0 => loop(v + 1.0)
      case v => Normalized(v)
    }

    loop(value)
  }
}
