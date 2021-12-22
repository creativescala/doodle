package doodle
package core

/** A Coordinate represents a position on an axis relative to a bounding box's
  * origin. Coordinates can be specified as a (1D) point or as a percentage
  * relative to the edge of the bounding box.
  *
  * For example, `Coordinate.point(10)` is ten units from the origin in the
  * positive direction, while `Coordinate.percent(100)` is the positive (top or
  * right) edge of the bounding box.
  */
sealed trait Coordinate {
  import Coordinate._

  def add(that: Coordinate): Coordinate =
    Add(this, that)

  def +(that: Coordinate): Coordinate =
    Add(this, that)

  def subtract(that: Coordinate): Coordinate =
    Subtract(this, that)

  def -(that: Coordinate): Coordinate =
    Subtract(this, that)

  /** Evaluate this Coordinate given values for -100% and +100% */
  def eval(negative: Double, positive: Double): Double =
    this match {
      case Percent(value) =>
        if (value < 0) Math.abs(value) * negative
        else value * positive

      case Point(value) => value

      case Add(l, r) => l.eval(negative, positive) + r.eval(negative, positive)

      case Subtract(l, r) =>
        l.eval(negative, positive) - r.eval(negative, positive)
    }
}
object Coordinate {

  /** Value is normalized so 100 percent is 1.0 */
  final case class Percent(value: Double) extends Coordinate
  final case class Point(value: Double) extends Coordinate
  final case class Add(left: Coordinate, right: Coordinate) extends Coordinate
  final case class Subtract(left: Coordinate, right: Coordinate)
      extends Coordinate

  def percent(value: Double): Coordinate = Percent(value / 100.0)
  def point(value: Double): Coordinate = Point(value)
  val zero = point(0.0)
  val oneHundredPercent = percent(100)
  val minusOneHundredPercent = percent(-100)
}
