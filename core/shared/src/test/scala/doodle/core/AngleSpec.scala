package doodle
package core

import org.scalacheck._
import org.scalacheck.Prop._

object AngleSpec extends Properties("Angle properties") {
  import doodle.arbitrary._
  import doodle.syntax.approximatelyEqual._

  property("angle has bijection to Double as radians") =
    forAll{ (a: Angle) => a ~= Angle.radians(a.toRadians) }

  property("angle has bijection to Double as degrees") =
    forAll{ (a: Angle) => a ~= Angle.degrees(a.toDegrees) }

  property("angle has bijection to Double as turns") =
    forAll{ (a: Angle) => a ~= Angle.turns(a.toTurns) }

  property("angle negation is inverse") =
    forAll{ (a: Angle) => (a + (-a)) ~= Angle.zero }

  property("angle double negation is identity") =
    forAll{ (a: Angle) => a ~= -(-a) }
}
