package doodle

import utest._
import doodle.syntax.angle._

object AngleSpec extends TestSuite {
  val tests = TestSuite {
    "Conversions to/from degrees ok"-{
      for(i <- 1 to 100) {
        val original = Math.random() * 360
        val converted = (original.degrees).toDegrees
        assert(Math.abs(original - converted) < 1)
      }
    }
  }
}
