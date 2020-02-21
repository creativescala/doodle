package doodle
package golden

import doodle.image._
import munit._

class ImageLayout extends FunSuite with Golden {
  testImage("layout-at-debug") {
    val c = Image.circle(20)

    c.debug
      .beside(c.debug.at(5, 5))
      .beside(c.debug.at(5, -5))
      .above(c.debug.beside(c.at(5, 5).debug).beside(c.at(5, -5).debug))
      .above(c.beside(c.at(5, 5)).beside(c.at(5, -5)).debug)
  }
}
