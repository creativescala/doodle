package doodle
package core

import doodle.syntax.all._
import minitest._

object ColorSuite extends SimpleTestSuite {
  test("toRGBA should convert to expected RGBA color") {
    val blueHSLA = Color.hsl(240.degrees, 0.5, 0.5).toRGBA
    val blueRGBA = Color.rgb(64.uByte, 64.uByte, 191.uByte)
    assert(blueHSLA ~= blueRGBA)

    val greenHSLA = Color.hsl(120.degrees, 0.5, 0.5).toRGBA
    val greenRGBA = Color.rgb(64.uByte, 191.uByte, 64.uByte)
    assert(greenHSLA ~= greenRGBA)

    val redHSLA = Color.hsl(0.degrees, 0.75, 0.5).toRGBA
    val redRGBA = Color.rgb(223.uByte, 32.uByte, 32.uByte)
    assert(redHSLA ~= redRGBA)
  }

  test("toHSLA should converts to expected HSLA color") {
    val blueHSLA = Color.hsl(240.degrees, 0.5, 0.5)
    val blueRGBA = Color.rgb(64.uByte, 64.uByte, 191.uByte).toHSLA
    assert(blueHSLA ~= blueRGBA)

    val greenHSLA = Color.hsl(120.degrees, 0.5, 0.5)
    val greenRGBA = Color.rgb(64.uByte, 191.uByte, 64.uByte).toHSLA
    assert(greenHSLA ~= greenRGBA)

    val redHSLA = Color.hsl(0.degrees, 0.75, 0.5)
    val redRGBA = Color.rgb(223.uByte, 32.uByte, 32.uByte).toHSLA
    assert(redHSLA ~= redRGBA)
  }

  test("HSLA with 0 saturation should convert to gray RGBA") {
    val grey1HSLA = Color.hsl(0.degrees, 0, 0.5).toRGBA
    val grey1RGBA = Color.rgb(128.uByte, 128.uByte, 128.uByte)
    assert(grey1HSLA ~= grey1RGBA)

    val grey2HSLA = Color.hsl(0.degrees, 0, 1.0).toRGBA
    val grey2RGBA = Color.rgb(255.uByte, 255.uByte, 255.uByte)
    assert(grey2HSLA ~= grey2RGBA)
  }

  test("HSLA spin should transform correctly") {
    val original = Color.hsl(120.degrees, 0.5, 0.5)
    val spun = original.spin(60.degrees)
    val unspun = original.spin(-60.degrees)

    assert(spun ~= Color.hsl(180.degrees, 0.5, 0.5))
    assert(unspun ~= Color.hsl(60.degrees, 0.5, 0.5))
  }

  test("Fade in/out should transform correctly") {
    val original = Color.hsla(120.degrees, 0.5, 0.5, 0.5)
    val fadeOut = original.fadeOut(0.5.normalized)
    val fadeIn = original.fadeIn(0.5.normalized)

    assert(fadeOut.alpha == (0.0.normalized))
    assert(fadeIn.alpha == (1.0.normalized))
  }
}
