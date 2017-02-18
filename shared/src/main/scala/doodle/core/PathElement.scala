package doodle.core

sealed abstract class PathElement extends Product with Serializable {
  import PathElement._

  def transform(tx: doodle.core.transform.Transform): PathElement =
    this match {
      case MoveTo(to) => MoveTo(tx(to))
      case LineTo(to) => LineTo(tx(to))
      case BezierCurveTo(cp1, cp2, to) => BezierCurveTo(tx(cp1), tx(cp2), tx(to))
    }
}
object PathElement {
  final case class MoveTo(to: Point) extends PathElement
  final case class LineTo(to: Point) extends PathElement
  final case class BezierCurveTo(cp1: Point, cp2: Point, to: Point) extends PathElement

  def moveTo(point: Point): PathElement =
    MoveTo(point)

  def moveTo(x: Double, y: Double): PathElement =
    moveTo(Point.cartesian(x,y))

  def moveTo(r: Double, angle: Angle): PathElement =
    moveTo(Point.polar(r,angle))


  def lineTo(point: Point): PathElement =
    LineTo(point)

  def lineTo(x: Double, y: Double): PathElement =
    lineTo(Point.cartesian(x,y))

  def lineTo(r: Double, angle: Angle): PathElement =
    lineTo(Point.polar(r,angle))

  def curveTo(cp1: Point, cp2: Point, to: Point): PathElement =
    BezierCurveTo(cp1, cp2, to)

  def curveTo(cp1X: Double, cp1Y: Double, cp2X: Double, cp2Y: Double, toX: Double, toY: Double): PathElement =
    curveTo(
      Point.cartesian(cp1X, cp1Y),
      Point.cartesian(cp2X, cp2Y),
      Point.cartesian(toX,  toY)
    )

  def curveTo(cp1R: Double, cp1Angle: Angle, cp2R: Double, cp2Angle: Angle, toR: Double, toAngle: Angle): PathElement =
    curveTo(
      Point.polar(cp1R, cp1Angle),
      Point.polar(cp2R, cp2Angle),
      Point.polar(toR,  toAngle)
    )

}
