package doodle
package image
package examples

import cats.instances.list._
import doodle.core._
import doodle.image.Image
import doodle.image.syntax._
import doodle.syntax.all._

object Polygon {

  def polygon(sides: Int, radius: Double) = {
    import PathElement._

    val centerAngle = 360.degrees / sides.toDouble

    val elements = (0 until sides) map { index =>
      val point = Point.polar(radius, centerAngle * index.toDouble)
      if (index == 0) moveTo(point) else lineTo(point)
    }

    Image
      .closedPath(elements)
      .strokeWidth(5)
      .strokeColor(Color.hsl(centerAngle, 1.0, .5))
  }

  def image = ((3 to 20) map (polygon(_, 100))).toList.allOn
}
