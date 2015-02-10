package doodle.syntax

import utest._
import doodle.core.Normalized
import doodle.syntax.normalized._

object NormalizedSpec extends TestSuite {
  val tests = TestSuite {
    "syntax constructs expected normalizeds"-{
      assert(60.clip == Normalized.MaxValue)
      assert(1.clip == Normalized.MaxValue)
      assert(0.5.clip == Normalized.clip(0.5))
    }
  }
}
