package doodle.syntax

import utest._
import doodle.core.Angle

object AngleSpec extends TestSuite {
  val tests = TestSuite {
    "syntax constructs expected angles"-{
      assert(60.degrees == Angle.degrees(60))
      assert(1.radians == Angle.radians(1))
      assert(0.5.turns == Angle.turns(0.5))
    }
  }
}
