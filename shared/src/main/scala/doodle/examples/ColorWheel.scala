package doodle.examples

import doodle.core._
import doodle.syntax._

object ColorWheel extends Drawable {
  val blobs = (0 to 360 by 10).map { hue =>
    val angle = Angle.degrees(hue)
    Circle(20).
      at(50 * angle.sin, 50 * angle.cos).
      lineWidth(0).
      fillColor(Color.hsl(hue, 1, 0.5))
  }

  def draw = blobs.reduceLeft(_ on _)
}
