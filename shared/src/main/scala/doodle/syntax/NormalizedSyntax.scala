package doodle.syntax

import doodle.core.Normalized

trait NormalizedSyntax {
  implicit class ToNormalizedOps(val value: Double) {
    def normalized: Normalized =
      Normalized.clip(value)
  }

}
