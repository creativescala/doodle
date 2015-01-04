package doodle.syntax

import doodle.Normalised

object normalised {
  implicit class ToNormalisedOps(val normalised: Double) extends AnyVal {
    def clip: Normalised =
      Normalised.clip(normalised)
  }
}
