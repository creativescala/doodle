package doodle.examples

import doodle.core._
import doodle.syntax._

object Sierpinski extends Drawable {
  def sierpinski(size: Double, color: Color): Image = {
    if(size > 8) {
      val child = sierpinski(size/2, color)
      child above (child beside child)
    } else {
      Triangle(size, size) lineWidth 0 fillColor color
    }
  }

  def draw = sierpinski(512, Color.orange)
}