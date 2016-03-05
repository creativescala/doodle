package doodle
package backend

import doodle.core.{ContextTransform,BezierCurveTo,LineTo,MoveTo,Vec}

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
  val center = Vec((left + right) / 2, (top + bottom) / 2)

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

  def expand(toInclude: Vec): BoundingBox =
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
    val topLeft     = Vec(left, top) 
    val topRight    = Vec(right, top)
    val bottomLeft  = Vec(left, bottom)
    val bottomRight = Vec(right, bottom)

    List(topLeft, topRight, bottomLeft, bottomRight)
      .map(_ + offset)
      .foldLeft(this){ (bb, point) => bb.expand(point) }
  }
}

object BoundingBox {
  val empty: BoundingBox = BoundingBox(0.0, 0.0, 0.0, 0.0)

  def apply(vec: Vec): BoundingBox =
    BoundingBox(vec.x, vec.y, vec.x, vec.y)

  def apply(vecs: Seq[Vec]): BoundingBox =
    vecs match {
      case Seq()    => BoundingBox.empty
      case Seq(hd)  => BoundingBox(hd)
      case hd +: tl => tl.foldLeft(BoundingBox(hd))(_ expand _)
    }

  def apply(image: Image): BoundingBox =
    image match {
      case Path(ctx, elts) =>
        BoundingBox(elts.flatMap {
                      case MoveTo(pos) => Seq(pos)
                      case LineTo(pos) => Seq(pos)
                      case BezierCurveTo(cp1, cp2, pos) =>
                        // The control points form a bounding box around a bezier curve,
                        // but this may not be a tight bounding box.
                        // It's an acceptable solution for now but in the future
                        // we may wish to generate a tighter bounding box.
                        Seq(cp1, cp2, pos)
                    })

      case On(t, b) =>
        val BoundingBox(l1, t1, r1, b1) = t.boundingBox
        val BoundingBox(l2, t2, r2, b2) = b.boundingBox
        BoundingBox(l1 min l2, t1 max t2, r1 max r2, b1 min b2)

      case Beside(l, r) =>
        val boxL = l.boundingBox
        val boxR = r.boundingBox
        BoundingBox(
          -(boxL.width + boxR.width) / 2,
          boxL.top max boxR.top,
          (boxL.width + boxR.width) / 2,
          boxL.bottom min boxR.bottom
        )

      case Above(t, b) =>
        val boxT = t.boundingBox
        val boxB = b.boundingBox

        BoundingBox(
          boxT.left min boxB.left,
          (boxT.height + boxB.height) / 2,
          boxT.right max boxB.right,
          -(boxT.height + boxB.height) / 2
        )

      case At(v, i) =>
        i.boundingBox
        //i.boundingBox translate v

      case Empty =>
        BoundingBox.empty
    }
}
