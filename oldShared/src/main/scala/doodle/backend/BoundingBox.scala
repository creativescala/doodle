package doodle
package backend

import doodle.core.{Point, Vec}
import doodle.core.transform.Transform

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
  * outside the box. There is a convention for placement of the origin that is
  * relied upon for layout:
  *
  *  - for a Path, the origin may be anywhere; otherwise
  *
  *  - layout operations align origins of the bounding boxex they align along
  *    one or more dimensions. The origin of the bounding box of such an
  *    operation is similarly aligned along the relevant dimension and centered
  *    on any dimension that is not aligned.
  *
  * An example shows the above more clearly: a Beside aligns the y coordinates of
  * the elements it encloses, and its own origin is aligned along the y-axis
  * with the enclosing elements and centered on the x-axis.
  *
  * Coordinates follow the usual Cartesian system (+ve Y is up, and +ve X is
  * right) not the common computer graphic coordinate system (+ve Y is down).
  */
final case class BoundingBox(left: Double, top: Double, right: Double, bottom: Double) {
  val topLeft     = Point.cartesian(left, top)
  val topRight    = Point.cartesian(right, top)
  val bottomLeft  = Point.cartesian(left, bottom)
  val bottomRight = Point.cartesian(right, bottom)

  val height: Double =
    top - bottom

  val width: Double =
    right - left

  val center = Point.cartesian(left + (width / 2), bottom + (height / 2))

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

  def beside(that: BoundingBox): BoundingBox = {
    val width = this.width + that.width
    val top   = this.top max that.top
    val bottom = this.bottom min that.bottom

    BoundingBox(
      -(width / 2),
      top,
      width / 2,
      bottom
    )
  }

  def on(that: BoundingBox): BoundingBox =
    BoundingBox(
      this.left   min that.left,
      this.top    max that.top,
      this.right  max that.right,
      this.bottom min that.bottom
    )

  def above(that: BoundingBox): BoundingBox = {
    val height = this.height + that.height
    val right = this.right max that.right
    val left = this.left min that.left

    BoundingBox(
      left,
      height / 2,
      right,
      -(height / 2)
    )
  }

  def at(offset: Vec): BoundingBox = {
    List(topLeft, topRight, bottomLeft, bottomRight)
      .map(_ + offset)
      .foldLeft(this){ (bb, point) => bb.expand(point) }
  }

  def transform(transform: Transform): BoundingBox = {
    BoundingBox.empty.
      expand(transform(topLeft)).
      expand(transform(topRight)).
      expand(transform(bottomLeft)).
      expand(transform(bottomRight))
  }
}

object BoundingBox {
  val empty: BoundingBox = BoundingBox(0.0, 0.0, 0.0, 0.0)

  def apply(point: Point): BoundingBox =
    BoundingBox(point.x, point.y, point.x, point.y)

  def apply(points: Seq[Point]): BoundingBox =
    points.foldLeft(BoundingBox.empty){ (bb, elt) => bb.expand(elt) }
}
