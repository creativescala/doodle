package doodle
package syntax

import doodle.core.Normalized
import org.scalatest._
import org.scalatest.prop.Checkers

class NormalizedSpec extends FlatSpec with Matchers with Checkers {
  "syntax" should "construct expected normalizeds" in {
    60.normalized should ===(Normalized.MaxValue)
    1.normalized should ===(Normalized.MaxValue)
    0.5.normalized should ===(Normalized.clip(0.5))
  }
}
