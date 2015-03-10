package doodle.examples

import doodle.core._
import doodle.syntax._

object Koch extends Drawable {
  def koch(depth: Int, start: Vec, angle: Angle, length: Double): Seq[PathElement] = {
    if(depth == 0) {
      Seq(LineTo(start + Vec.polar(angle, length)))
    } else {
      val lAngle = angle - 60.degrees
      val rAngle = angle + 60.degrees

      val third  = length / 3.0
      val edge   = Vec.polar(angle, third)

      val mid1 = start + edge
      val mid2 = mid1 + edge.rotate(-60.degrees)
      val mid3 = mid2 + edge.rotate( 60.degrees)
      val end  = mid3 + edge

      koch(depth-1, start,  angle, third) ++
      koch(depth-1,  mid1, lAngle, third) ++
      koch(depth-1,  mid2, rAngle, third) ++
      koch(depth-1,  mid3,  angle, third)
    }
  }

  def draw = {
    val size  = 405
    val depth = 4
    Path(koch(depth, Vec.zero, 0.degrees, size))
  }
}
