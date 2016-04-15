package doodle
package backend

import doodle.core.{ContextTransform,BezierCurveTo,LineTo,MoveTo,Point,Vec}

/**
  * A `BoundingBox` serves two purposes:
  *
  * - it defines the extent (width and height) of an `Image`; and
  *
  * - it defines a *local* coordinate system based on an origin that *must* be
  *   contained within the `BoundingBox`.
  *
  * Together this information is used to layout an Image.
  *
  * Note the origin of the local coordinate system can be any point that is not
  * outside the box. It is typically at the center of the box, but it need not
  * be there.
  *
  * Coordinates follow the usual Cartesian system (+ve Y is up, and +ve X is
  * right) not the common computer graphic coordinate system (+ve Y is down).
  */
final case class BoundingBox(left: Double, top: Double, right: Double, bottom: Double) {
  val center = Point.cartesian((left + right) / 2, (top + bottom) / 2)

  val height: Double =
    top - bottom

  val width: Double =
    right - left

  def pad(padding: Double): BoundingBox =
    BoundingBox(
      left   - padding,
      top    + padding,
      right  + padding,
      bottom - padding
    )

  def expand(toInclude: Point): BoundingBox =
    BoundingBox(
      left   min toInclude.x,
      top    max toInclude.y,
      right  max toInclude.x,
      bottom min toInclude.y
    )

  def beside(that: BoundingBox): BoundingBox =
    BoundingBox(
      -(this.width + that.width) / 2,
      this.top max that.top,
      (this.width + that.width) / 2,
      this.bottom min that.bottom
    )

  def on(that: BoundingBox): BoundingBox = 
    BoundingBox(
      this.left   min that.left,
      this.top    max that.top,
      this.right  max that.right,
      this.bottom min that.bottom
    )

  def above(that: BoundingBox): BoundingBox =
    BoundingBox(
      this.left min that.left,
      (this.height + that.height) / 2,
      this.right max that.right,
      -(this.height + that.height) / 2
    )

  def at(offset: Vec): BoundingBox = {
    val topLeft     = Point.cartesian(left, top) 
    val topRight    = Point.cartesian(right, top)
    val bottomLeft  = Point.cartesian(left, bottom)
    val bottomRight = Point.cartesian(right, bottom)

    List(topLeft, topRight, bottomLeft, bottomRight)
      .map(_ + offset)
      .foldLeft(this){ (bb, point) => bb.expand(point) }
  }
}

object BoundingBox {
  val empty: BoundingBox = BoundingBox(0.0, 0.0, 0.0, 0.0)

  def apply(point: Point): BoundingBox =
    BoundingBox(point.x, point.y, point.x, point.y)

  def apply(points: Seq[Point]): BoundingBox =
    points match {
      case Seq()    => BoundingBox.empty
      case Seq(hd)  => BoundingBox(hd)
      case hd +: tl => tl.foldLeft(BoundingBox(hd))(_ expand _)
    }
}
