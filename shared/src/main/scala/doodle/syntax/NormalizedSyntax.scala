package doodle.syntax

import doodle.core.Normalized
import scala.language.implicitConversions

class ToNormalizedOps(val value: Double) extends AnyVal {
  def normalized: Normalized =
    Normalized.clip(value)
}

trait NormalizedSyntax {
  implicit def doubleToNormalizedOps(number: Double): ToNormalizedOps =
    new ToNormalizedOps(number)
}
