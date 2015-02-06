package doodle.syntax

import doodle.core.Normalised

object normalised {
  implicit class ToNormalisedOps(val normalised: Double) extends AnyVal {
    def clip: Normalised =
      Normalised.clip(normalised)
  }
}
