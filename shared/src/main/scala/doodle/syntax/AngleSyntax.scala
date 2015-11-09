package doodle.syntax

import doodle.core.Angle

trait AngleSyntax {
  implicit class AngleOps(val angle: Double) {
    def degrees: Angle =
      Angle.degrees(angle)

    def radians: Angle =
      Angle.radians(angle)

    def turns: Angle =
      Angle.turns(angle)
  }
}
