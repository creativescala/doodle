package doodle
package core

import org.scalacheck.{ Arbitrary, Gen }
import org.scalatest._
import org.scalatest.prop.Checkers

class AngleSpec extends FlatSpec with Matchers with Checkers {
  implicit val arbitraryAngle: Arbitrary[Angle] = Arbitrary(
    Gen.choose(-36.0, 36.0).map { angle => Angle(angle) }
  )

  def nearlyEqual(a1: Angle, a2: Angle): Boolean =
    Math.abs((a1 - a2).toRadians) < 0.01

  "Angle" should "have bijection to Double as radians" in {
    check { (a: Angle) =>
      nearlyEqual(a, Angle.radians(a.toRadians))
    }
  }

  "Angle" should "have bijection to Double as degrees" in {
    check { (a: Angle) =>
      nearlyEqual(a, Angle.degrees(a.toDegrees))
    }
  }

  "Angle" should "have bijection to Double as turns" in {
    check { (a: Angle) =>
      nearlyEqual(a, Angle.turns(a.toTurns))
    }
  }
}
