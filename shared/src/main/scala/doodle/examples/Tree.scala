package doodle.examples

import doodle.core._
import doodle.syntax._

object Tree {
  def leaf(angle: Angle, length: Double): Image =
    OpenPath(Seq(
      MoveTo(Point.zero),
      LineTo(Point.polar(length, angle))
    )) lineColor Color.hsl(angle, .5.normalized, .5.normalized)

  def branch(depth: Int, angle: Angle, length: Double): Image = {
    if(depth == 0) {
      leaf(angle, length)
    } else {
      val l = branch(depth - 1, angle + 20.degrees, length * 0.8)
      val r = branch(depth - 1, angle - 20.degrees, length * 0.8)
      val b = leaf(angle, length)
      b on ((l on r) at Vec.polar(angle, length))
    }
  }

  def image = branch(10, 90.degrees, 50)
}
