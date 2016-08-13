package doodle.examples

import doodle.core._
import doodle.syntax._

object Stars {
  def star(sides: Int, skip: Int, radius: Double) = {
    import PathElement._

    val centerAngle = 360.degrees * skip / sides

    val elements = (0 to sides) map { index =>
      val pt = Point.polar(radius, centerAngle * index)
      if(index == 0)
        moveTo(pt)
      else
        lineTo(pt)
    }

    Image.openPath(elements).
      lineWidth(2).
      lineColor(Color.hsl(centerAngle, 1.normalized, .25.normalized)).
      fillColor(Color.hsl(centerAngle, 1.normalized, .75.normalized))
  }

  val image =
    allAbove((3 to 33 by 2) map { sides =>
      allBeside((1 to sides/2) map { skip =>
        star(sides, skip, 20)
      })
    })
}
