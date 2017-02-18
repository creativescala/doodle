package doodle
package core

import org.scalatest._
import org.scalatest.prop.Checkers

class AngleSpec extends FlatSpec with Matchers with Checkers {
  import doodle.arbitrary._
  import doodle.syntax.approximatelyEqual._

  "Angle" should "have bijection to Double as radians" in {
    check { (a: Angle) =>
      a ~= Angle.radians(a.toRadians)
    }
  }

  "Angle" should "have bijection to Double as degrees" in {
    check { (a: Angle) =>
      a ~= Angle.degrees(a.toDegrees)
    }
  }

  "Angle" should "have bijection to Double as turns" in {
    check { (a: Angle) =>
      a ~= Angle.turns(a.toTurns)
    }
  }
}
