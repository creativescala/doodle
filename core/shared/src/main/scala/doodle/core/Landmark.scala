package doodle
package core

/** A Landmark represents a position relative to the origin of a bounding box.
  */
final case class Landmark(x: Coordinate, y: Coordinate)
object Landmark {

  def percent(x: Double, y: Double): Landmark =
    Landmark(Coordinate.percent(x), Coordinate.percent(y))

  def point(x: Double, y: Double): Landmark =
    Landmark(Coordinate.point(x), Coordinate.point(y))

  val origin = Landmark(Coordinate.zero, Coordinate.zero)
  val topLeft =
    Landmark(Coordinate.minusOneHundredPercent, Coordinate.oneHundredPercent)
  val topRight =
    Landmark(Coordinate.oneHundredPercent, Coordinate.oneHundredPercent)
  val bottomLeft =
    Landmark(
      Coordinate.minusOneHundredPercent,
      Coordinate.minusOneHundredPercent
    )
  val bottomRight =
    Landmark(
      Coordinate.oneHundredPercent,
      Coordinate.minusOneHundredPercent
    )
}
