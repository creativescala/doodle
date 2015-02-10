package doodle.examples

import doodle.core._
import doodle.syntax._

case class Orbit(angle: Angle, radius: Double, image: Image) extends Animation {
  def animate = this.copy(angle + 5.degrees)
  def draw: Image = {
    image.at(
      radius * math.sin(angle.toRadians),
      radius * math.cos(angle.toRadians)
    )
  }
}

object Orbit extends Orbit(Angle(0), 30, Circle(10) fillColor Color.orange)