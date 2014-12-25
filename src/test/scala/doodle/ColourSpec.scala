package doodle

import utest._

object ColourSpec extends TestSuite {
  val tests = TestSuite {
    "Known HSLA converts to expected RGBA"-{
      val greenHSLA = Colour.hsla(120, 0.5, 0.5, 1.0).toRGBA
      val greenRGBA = Colour.rgba(64, 191, 64, 1.0)
      assert(greenHSLA == greenRGBA)
    }
  }
}
