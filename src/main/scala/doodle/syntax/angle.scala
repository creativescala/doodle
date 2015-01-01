package doodle.syntax

import doodle.Angle

object angle {
  implicit class ToAngleOps(val angle: Double) extends AnyVal {
    def degrees: Angle =
      Angle.degrees(angle)
    def radians: Angle =
      Angle.radians(angle)
    def turns: Angle =
      Angle.turns(angle)
  }
}
