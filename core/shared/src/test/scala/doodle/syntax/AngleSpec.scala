package doodle
package syntax

import doodle.core.Angle
import org.scalacheck.Prop._
import org.scalacheck._

class AngleSpec extends Properties("Angle syntax properties") {
  property(".degrees") = forAll { (d: Double) => d.degrees ?= Angle.degrees(d) }

  property(".radians") = forAll { (d: Double) => d.radians ?= Angle.radians(d) }

  property(".turns") = forAll { (d: Double) => d.turns ?= Angle.turns(d) }
}
