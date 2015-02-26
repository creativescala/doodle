package doodle.core

import utest._
import doodle.syntax.angle._

object AngleSpec extends TestSuite {
  implicit val ec = utest.ExecutionContext.RunNow

  val tests = TestSuite {
    "Conversions to/from degrees ok"-{
      for(i <- 1 to 100) {
        val original = Math.random() * 360
        val converted = (original.degrees).toDegrees
        assert(Math.abs(original - converted) < 1)
      }
    }

    "Conversions to/from normalized"-{
      for(i <- 1 to 100) {
        val original = Angle.turns(Math.random)
        val converted = Angle.turns(original.toTurns.get)

        assert(Math.abs(original.toRadians - converted.toRadians) < 0.1)
      }
    }
  }
}
