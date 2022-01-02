package doodle
package golden

import doodle.core.Color
import doodle.java2d._
import doodle.syntax.all._
import munit._

class FrameBackground extends FunSuite with GoldenPicture {
  testPictureWithFrame("black-background")(
    Frame.fitToPicture().background(Color.black)
  ) {
    circle[Algebra, Drawing](20).fillColor(Color.white)
  }

  testPictureWithFrame("red-background")(
    Frame.fitToPicture().background(Color.red)
  ) {
    circle[Algebra, Drawing](20).fillColor(Color.white)
  }
}
