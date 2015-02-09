package doodle.syntax

import doodle.core.Angle
import scala.language.implicitConversions

class ToAngleOps(val angle: Double) extends AnyVal {
  def degrees: Angle =
    Angle.degrees(angle)

  def radians: Angle =
    Angle.radians(angle)

  def turns: Angle =
    Angle.turns(angle)
}

trait AngleSyntax {
  implicit def angleToAngleOps(angle: Double): ToAngleOps =
    new ToAngleOps(angle)
}
