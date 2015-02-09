package doodle.syntax

import doodle.core.Normalised
import scala.language.implicitConversions

class ToNormalisedOps(val normalised: Double) extends AnyVal {
  def clip: Normalised =
    Normalised.clip(normalised)
}

trait NormalisedSyntax {
  implicit def doubleToNormalisedOps(number: Double): ToNormalisedOps =
    new ToNormalisedOps(number)
}
