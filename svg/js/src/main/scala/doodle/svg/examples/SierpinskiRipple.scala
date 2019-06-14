package doodle
package svg
package examples

import cats.implicits._
import doodle.core._
import doodle.syntax._

object SierpinskiRipple {
  def base(size: Double, color: Color): Picture[Unit] = {
    Picture{ implicit algebra =>
      import algebra._
      triangle(size, size).fillColor(color).strokeColor(color)
    }
  }

  def sierpinski(n: Int, size: Double, color: Color): Picture[Unit] = {
    if (n == 1) {
      base(size, color)
    } else {
      sierpinski(n-1, size / 2, color.spin(-10.degrees))
        .above(sierpinski(n-1, size / 2, color.spin(37.degrees))
                 .beside(sierpinski(n-1, size / 2, color.spin(79.degrees))))
    }
  }

  val image = sierpinski(6, 512, Color.brown)
}
