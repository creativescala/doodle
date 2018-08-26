package doodle
package core

import doodle.syntax._
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class ColorSpec extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {
  import doodle.arbitrary._
  import doodle.syntax.approximatelyEqual._

  "Angle" should "have bijection to Double as radians" in {
    forAll { (a: Angle) =>
      assert(a ~= Angle.radians(a.toRadians))
    }
  }

  "Angle" should "have bijection to Double as degrees" in {
    forAll { (a: Angle) =>
      assert(a ~= Angle.degrees(a.toDegrees))
    }
  }

  "Angle" should "have bijection to Double as turns" in {
    forAll { (a: Angle) =>
      assert(a ~= Angle.turns(a.toTurns))
    }
  }

  "toRGBA" should "convert to expected RGBA color" in {
    val blueHSLA = Color.hsl(240.degrees, 0.5.normalized, 0.5.normalized).toRGBA
    val blueRGBA = Color.rgb(64.uByte, 64.uByte, 191.uByte)
    assert(blueHSLA ~= blueRGBA)

    val greenHSLA = Color.hsl(120.degrees, 0.5.normalized, 0.5.normalized).toRGBA
    val greenRGBA = Color.rgb(64.uByte, 191.uByte, 64.uByte)
    assert(greenHSLA ~= greenRGBA)

    val redHSLA = Color.hsl(0.degrees, 0.75.normalized, 0.5.normalized).toRGBA
    val redRGBA = Color.rgb(223.uByte, 32.uByte, 32.uByte)
    assert(redHSLA ~= redRGBA)
  }

  "toHSLA"  should "converts to expected HSLA color" in {
    val blueHSLA = Color.hsl(240.degrees, 0.5.normalized, 0.5.normalized)
    val blueRGBA = Color.rgb(64.uByte, 64.uByte, 191.uByte).toHSLA
    assert(blueHSLA ~= blueRGBA)

    val greenHSLA = Color.hsl(120.degrees, 0.5.normalized, 0.5.normalized)
    val greenRGBA = Color.rgb(64.uByte, 191.uByte, 64.uByte).toHSLA
    assert(greenHSLA ~= greenRGBA)

    val redHSLA = Color.hsl(0.degrees, 0.75.normalized, 0.5.normalized)
    val redRGBA = Color.rgb(223.uByte, 32.uByte, 32.uByte).toHSLA
    assert(redHSLA ~= redRGBA)
  }

  "HSLA with 0 saturation" should "convert to gray RGBA" in {
    val grey1HSLA = Color.hsl(0.degrees, 0.normalized, 0.5.normalized).toRGBA
    val grey1RGBA = Color.rgb(128.uByte, 128.uByte, 128.uByte)
    assert(grey1HSLA ~= grey1RGBA)

    val grey2HSLA = Color.hsl(0.degrees, 0.normalized, 1.0.normalized).toRGBA
    val grey2RGBA = Color.rgb(255.uByte, 255.uByte, 255.uByte)
    assert(grey2HSLA ~= grey2RGBA)
  }

  "HSLA spin" should "transform correctly" in {
    val original = Color.hsl(120.degrees, 0.5.normalized, 0.5.normalized)
    val spun = original.spin(60.degrees)
    val unspun = original.spin(-60.degrees)

    assert(spun ~= Color.hsl(180.degrees, 0.5.normalized, 0.5.normalized))
    assert(unspun ~= Color.hsl(60.degrees, 0.5.normalized, 0.5.normalized))
  }

  "Fade in/out" should "transform correctly" in {
    val original = Color.hsla(120.degrees, 0.5.normalized, 0.5.normalized, 0.5.normalized)
    val fadeOut = original.fadeOut(0.5.normalized)
    val fadeIn = original.fadeIn(0.5.normalized)

    fadeOut.alpha should ===(0.0.normalized)
    fadeIn.alpha should ===(1.0.normalized)
  }

  ".toRGBA andThen .toHSLA" should "be the identity" in {
    forAll { (hsla: HSLA) =>
      assert(hsla ~= (hsla.toRGBA.toHSLA))
    }
  }

  ".toHSLA andThen .toRGBA" should "be the identity" in {
    forAll { (rgba: RGBA) =>
      assert(rgba ~= (rgba.toHSLA.toRGBA))
    }
  }
}
