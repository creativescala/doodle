package doodle.examples

import doodle.core._
import doodle.syntax._

case class Orbit(base: Image, radius: Double = 50, angle: Angle = 0.degrees) extends Animation {
  def next = this.copy(angle = angle + 5.degrees)
  def image: Image = {
    base.at(
      radius * math.sin(angle.toRadians),
      radius * math.cos(angle.toRadians)
    )
  }
}

object Orbit extends Orbit(Image.circle(10) fillColor Color.orange, 50, 0.degrees)
