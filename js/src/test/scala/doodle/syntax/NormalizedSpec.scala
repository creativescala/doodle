package doodle.syntax

import utest._
import doodle.core.Normalized
import doodle.syntax.normalized._

object NormalizedSpec extends TestSuite {
  val tests = TestSuite {
    "syntax constructs expected normalizeds"-{
      assert(60.normalized == Normalized.MaxValue)
      assert(1.normalized == Normalized.MaxValue)
      assert(0.5.normalized == Normalized.clip(0.5))
    }
  }
}
