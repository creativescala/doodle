package doodle
package syntax

import doodle.core.Angle
import org.scalatest._
import org.scalatest.prop.Checkers

class AngleSpec extends FlatSpec with Matchers with Checkers {
  "syntax" should "construct expected angles" in {
    60.degrees should ===(Angle.degrees(60))
    1.radians should ===(Angle.radians(1))
    0.5.turns should ===(Angle.turns(0.5))
  }
}
