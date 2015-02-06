package doodle.core

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

    "Conversions to/from normalised"-{
      for(i <- 1 to 100) {
        val original = Angle.turns(Math.random)
        val converted = Angle.turns(original.toTurns.get)

        assert(Math.abs(original.get - converted.get) < 0.1)
      }
    }
  }
}
