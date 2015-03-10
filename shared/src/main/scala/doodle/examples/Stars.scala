package doodle.examples

import doodle.core._
import doodle.syntax._

object Stars extends Drawable {
  def star(sides: Int, skip: Int, radius: Double) = {
    val centerAngle = 360.degrees * skip / sides

    val elements = (0 to sides) map { index =>
      LineTo(Vec.polar(centerAngle * index, radius))
    }

    Path(elements).
      lineWidth(2).
      lineColor(Color.hsl(centerAngle, 1, .25)).
      fillColor(Color.hsl(centerAngle, 1, .75))
  }

  val draw =
    allAbove((3 to 33 by 2) map { sides =>
      allBeside((1 to sides/2) map { skip =>
        star(sides, skip, 20)
      })
    })
}
