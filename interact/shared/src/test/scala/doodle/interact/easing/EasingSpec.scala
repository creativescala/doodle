package doodle
package interact
package easing

import doodle.syntax.approximatelyEqual._
import org.scalacheck._
import org.scalacheck.Prop._

object EasingSpec extends Properties("Easing properties") {
  property("identity is the identity") = forAllNoShrink { (t: Double) =>
    Easing.identity(t) =? t
  }

  property("followedBy sends [0,0.5) to first function") =
    forAllNoShrink(Gen.chooseNum(0.0, 0.5).filter(_ != 0.5)) { (t: Double) =>
      val f = Easing.identity.followedBy(Easing.quadratic)
      f(t) =? t && (t >= 0.0) && (t <= 0.5)
    }

  property("followedBy sends [0.5,1.0] to second function") =
    forAllNoShrink(Gen.chooseNum(0.5, 1.0)) { (t: Double) =>
      val f = Easing.quadratic.followedBy(identity)
      (f(t) >= 0.5) && (f(t) <= 1.0)
    }

  property("reverse is its own inverse") =
    forAllNoShrink(Gen.chooseNum(0.0, 1.0)) { (t: Double) =>
      Easing.identity(t) ?= Easing.identity.reverse.reverse(t)
    }

  property("starts at 0") =
    (Easing.linear(0) ~= 0) &&
    (Easing.quadratic(0) ~= 0) &&
    (Easing.cubic(0) ~= 0) &&
    (Easing.sin(0) ~= 0) &&
    (Easing.circle(0) ~= 0) &&
    (Easing.back(0) ~= 0)

  property("ends at 1") =
    (Easing.linear(1) ~= 1) &&
    (Easing.quadratic(1) ~= 1) &&
    (Easing.cubic(1) ~= 1) &&
    (Easing.sin(1) ~= 1) &&
    (Easing.circle(1) ~= 1) &&
    (Easing.back(1) ~= 1)
}
