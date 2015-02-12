package doodle.core

import scala.annotation.tailrec

/**
  * An angle in radians, normalized to be in [0, 2pi]
  */
final case class Angle(toRadians: Double) extends AnyVal {
  def +(that: Angle): Angle =
    Angle.radians(this.toRadians + that.toRadians)

  def -(that: Angle): Angle =
    Angle.radians(this.toRadians - that.toRadians)

  def *(m: Double): Angle =
    Angle.radians(this.toRadians * m)

  def /(m: Double): Angle =
    Angle.radians(this.toRadians / m)

  def sin: Double =
    math.sin(toRadians)

  def cos: Double =
    math.cos(toRadians)

  /** Angle as the proportion of a full turn around a circle */
  def toTurns: Normalized =
    Normalized.clip(this.toRadians / Angle.TwoPi)

  def toDegrees: Double =
    (this.toRadians / Angle.TwoPi) * 360

  def toCanvas: String =
    this.toDegrees.toString
}
object Angle {
  val TwoPi = math.Pi * 2
  val MinValue = Angle(0.0)
  val MaxValue = Angle(TwoPi)

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
