package doodle
package image
package examples

import cats.instances.list._
import doodle.core._
import doodle.image.Image
import doodle.syntax._
import doodle.image.syntax._

object Stars {
  def star(sides: Int, skip: Int, radius: Double) = {
    import PathElement._

    val centerAngle = 360.degrees * skip.toDouble / sides.toDouble

    val elements = (0 to sides) map { index =>
      val pt = Point.polar(radius, centerAngle * index.toDouble)
      if (index == 0)
        moveTo(pt)
      else
        lineTo(pt)
    }

    Image
      .openPath(elements)
      .strokeWidth(2)
      .strokeColor(Color.hsl(centerAngle, 1, .25))
      .fillColor(Color.hsl(centerAngle, 1, .75))
  }

  val image =
    ((3 to 33 by 2) map { sides =>
      ((1 to sides / 2) map { skip =>
        star(sides, skip, 20)
      }).toList.allBeside
    }).toList.allAbove
}
