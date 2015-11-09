package doodle.examples

import doodle.core._
import doodle.syntax._

object Polygon {
  def polygon(sides: Int, radius: Double) = {
    val centerAngle = 360.degrees / sides

    val elements = (0 until sides) map { index =>
      val point = Vec.polar(centerAngle * index, radius)
      if(index == 0) MoveTo(point) else LineTo(point)
    }

    Path(elements) lineWidth 5 lineColor Color.hsl(centerAngle, 1.normalized, .5.normalized)
  }

  def image = allOn((3 to 20) map (polygon(_, 100)))
}

