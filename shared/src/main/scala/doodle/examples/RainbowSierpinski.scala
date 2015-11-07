package doodle.examples

import doodle.core._
import doodle.syntax._

object RainbowSierpinski {
  def sierpinski(size: Double, color: Color): Image = {
    if(size > 8) {
      val delta = 120.degrees * (size / 512.0)
      sierpinski(size/2, color.spin(delta)) above (
        sierpinski(size/2, color.spin(delta * 2)) beside
        sierpinski(size/2, color.spin(delta * 3))
      )
    } else {
      Triangle(size, size) lineWidth 0 fillColor color
    }
  }

  def image = sierpinski(512, Color.red)
}
