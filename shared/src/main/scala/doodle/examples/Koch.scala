package doodle.examples

import doodle.core._
import doodle.syntax._

object Koch {
  def kochElements(depth: Int, start: Vec, angle: Angle, length: Double): Seq[PathElement] = {
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

      kochElements(depth-1, start,  angle, third) ++
      kochElements(depth-1,  mid1, lAngle, third) ++
      kochElements(depth-1,  mid2, rAngle, third) ++
      kochElements(depth-1,  mid3,  angle, third)
    }
  }

  def koch(depth: Int, length: Double): Image = {
    val origin = Vec(0, length/6)
    Path(MoveTo(origin) +: kochElements(depth, origin, 0.degrees, length))
  }

  val image = allAbove((1 to 4) map { depth =>
    koch(depth, 512)
  })
}
