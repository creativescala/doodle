package doodle
package image
package examples

import doodle.core._
import doodle.syntax._
import doodle.image.Image

object SierpinskiRipple {
  def triangle(size: Double, color: Color): Image = {
    Image.equilateralTriangle(size).fillColor(color).strokeColor(color)
  }

  def sierpinski(n: Int, size: Double, color: Color): Image = {
    if (n == 1) {
      triangle(size, color)
    } else {
      sierpinski(n - 1, size / 2, color.spin(-10.degrees))
        .above(
          sierpinski(n - 1, size / 2, color.spin(37.degrees))
            .beside(sierpinski(n - 1, size / 2, color.spin(79.degrees))))
    }
  }

  val image = sierpinski(10, 1024, Color.brown)
}
