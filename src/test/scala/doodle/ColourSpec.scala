package doodle

import utest._
import doodle.syntax.angle._

object ColourSpec extends TestSuite {
  val tests = TestSuite {
    "Known HSLA converts to expected RGBA"-{
      val greenHSLA = Colour.hsla(120, 0.5, 0.5, 1.0).toRGBA
      val greenRGBA = Colour.rgba(64, 191, 64, 1.0)
      assert(greenHSLA == greenRGBA)

      val redHSLA = Colour.hsla(0, 0.75, 0.5, 1.0).toRGBA
      val redRGBA = Colour.rgba(223, 32, 32, 1.0)
      assert(redHSLA == redRGBA)
    }

    "HSLA spin transforms correctly"-{
      val original = Colour.hsla(120, 0.5, 0.5, 1.0)
      val spun = original.spin(60.degrees)
      val unspun = original.spin(-60.degrees)

      assert(spun ~= Colour.hsla(180, 0.5, 0.5, 1.0))
      assert(unspun ~= Colour.hsla(60, 0.5, 0.5, 1.0))
    }
  }
}
