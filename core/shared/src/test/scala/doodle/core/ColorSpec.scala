package doodle
package core

import org.scalacheck._
import org.scalacheck.Prop._

object ColorSpec extends Properties("Color properties") {
  import doodle.arbitrary._
  import Color._

  property(".toRGBA andThen .toHSLA is the identity") =
    forAll{ (hsla: HSLA) =>
      (hsla ~= (hsla.toRGBA.toHSLA))
    }

  property(".toHSLA andThen .toRGBA is the identity") =
    forAll{ (rgba: RGBA) =>
      (rgba ~= (rgba.toHSLA.toRGBA))
    }
}

