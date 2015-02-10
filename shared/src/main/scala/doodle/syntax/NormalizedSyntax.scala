package doodle.syntax

import doodle.core.Normalized
import scala.language.implicitConversions

class ToNormalizedOps(val normalized: Double) extends AnyVal {
  def clip: Normalized =
    Normalized.clip(normalized)
}

trait NormalizedSyntax {
  implicit def doubleToNormalizedOps(number: Double): ToNormalizedOps =
    new ToNormalizedOps(number)
}
