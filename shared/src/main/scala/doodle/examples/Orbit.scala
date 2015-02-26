package doodle.examples

import doodle.core._
import doodle.syntax._

case class Orbit(image: Image, radius: Double = 50, angle: Angle = 0.degrees) extends Animation {
  def animate = this.copy(angle = angle + 5.degrees)
  def draw: Image = {
    image.at(
      radius * math.sin(angle.toRadians),
      radius * math.cos(angle.toRadians)
    )
  }
}

object Orbit extends Orbit(Circle(10) fillColor Color.orange, 50, 0.degrees)