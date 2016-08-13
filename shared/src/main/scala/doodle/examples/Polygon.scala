package doodle.examples

import doodle.core._
import doodle.syntax._

object Polygon {

  def polygon(sides: Int, radius: Double) = {
    import PathElement._

    val centerAngle = 360.degrees / sides

    val elements = (0 until sides) map { index =>
      val point = Point.polar(radius, centerAngle * index)
      if(index == 0) moveTo(point) else lineTo(point)
    }

    Image.closedPath(elements) lineWidth 5 lineColor Color.hsl(centerAngle, 1.normalized, .5.normalized)
  }

  def image = allOn((3 to 20) map (polygon(_, 100)))
}

