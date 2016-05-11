package doodle.core

sealed abstract class PathElement extends Product with Serializable
object PathElement {
  def moveTo(point: Point): PathElement =
    MoveTo(point)

  def moveTo(x: Double, y: Double): PathElement =
    MoveTo(Point.cartesian(x,y))


  def lineTo(point: Point): PathElement =
    LineTo(point)

  def lineTo(x: Double, y: Double): PathElement =
    LineTo(Point.cartesian(x,y))


  def curveTo(cp1: Point, cp2: Point, to: Point): PathElement =
    BezierCurveTo(cp1, cp2, to)

  def curveTo(cp1X: Double, cp1Y: Double, cp2X: Double, cp2Y: Double, toX: Double, toY: Double): PathElement =
    BezierCurveTo(
      Point.cartesian(cp1X, cp1Y),
      Point.cartesian(cp2X, cp2Y),
      Point.cartesian(toX,  toY)
    )
}
final case class MoveTo(to: Point) extends PathElement
final case class LineTo(to: Point) extends PathElement
final case class BezierCurveTo(cp1: Point, cp2: Point, to: Point) extends PathElement
