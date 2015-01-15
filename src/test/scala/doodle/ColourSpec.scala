package doodle

import utest._
import doodle.syntax.angle._
import doodle.syntax.normalised._

object ColourSpec extends TestSuite {
  val tests = TestSuite {
    "Known HSLA converts to expected RGBA"-{
      val blueHSLA = Colour.hsla(240, 0.5, 0.5, 1.0).toRGBA
      val blueRGBA = Colour.rgba(64, 64, 191, 1.0)
      assert(blueHSLA ~= blueRGBA)

      val greenHSLA = Colour.hsla(120, 0.5, 0.5, 1.0).toRGBA
      val greenRGBA = Colour.rgba(64, 191, 64, 1.0)
      assert(greenHSLA ~= greenRGBA)

      val redHSLA = Colour.hsla(0, 0.75, 0.5, 1.0).toRGBA
      val redRGBA = Colour.rgba(223, 32, 32, 1.0)
      assert(redHSLA ~= redRGBA)
    }

    "Known RGBA converts to expected HSLA"-{
      val blueHSLA = Colour.hsla(240, 0.5, 0.5, 1.0)
      val blueRGBA = Colour.rgba(64, 64, 191, 1.0).toHSLA
      assert(blueHSLA ~= blueRGBA)

      val greenHSLA = Colour.hsla(120, 0.5, 0.5, 1.0)
      val greenRGBA = Colour.rgba(64, 191, 64, 1.0).toHSLA
      assert(greenHSLA ~= greenRGBA)

      val redHSLA = Colour.hsla(0, 0.75, 0.5, 1.0)
      val redRGBA = Colour.rgba(223, 32, 32, 1.0).toHSLA
      assert(redHSLA ~= redRGBA)
    }

    "HSLA with 0 saturation converts to gray"-{
      val grey1HSLA = Colour.hsla(0, 0, 0.5, 1.0).toRGBA
      val grey1RGBA = Colour.rgba(128, 128, 128, 1.0)
      assert(grey1HSLA ~= grey1RGBA)

      val grey2HSLA = Colour.hsla(0, 0, 1.0, 1.0).toRGBA
      val grey2RGBA = Colour.rgba(255, 255, 255, 1.0)
      assert(grey2HSLA ~= grey2RGBA)
    }

    "HSLA spin transforms correctly"-{
      val original = Colour.hsla(120, 0.5, 0.5, 1.0)
      val spun = original.spin(60.degrees)
      val unspun = original.spin(-60.degrees)

      assert(spun ~= Colour.hsla(180, 0.5, 0.5, 1.0))
      assert(unspun ~= Colour.hsla(60, 0.5, 0.5, 1.0))
    }

    "Fade in/out transforms correctly"-{
      val original = Colour.hsla(120, 0.5, 0.5, 0.5)
      val fadeOut = original fadeOut(0.5.clip)
      val fadeIn = original fadeIn(0.5.clip)

      assert(fadeOut.alpha == 0.0.clip)
      assert(fadeIn.alpha == 1.0.clip)
    }

    "Colours print to canvas correctly"-{
      val hsla = Colour.hsl(120, 0.5, 0.5).toCanvas
      assert(hsla == "hsla(120, 50%, 50%, 1)")
      val rgba = Colour.rgb(240, 12, 12).toCanvas
      assert(rgba == "rgba(240, 12, 12, 1)")
    }
  }
}
