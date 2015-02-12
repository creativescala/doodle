import doodle.core._
import doodle.examples._

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

@JSExport object Main extends JSApp {
  val image:Image = ChristmasTree
  val anim  = Orbit(image,50,Angle.radians(0))

  @JSExport def main(): Unit =
    doodle.js.animate(anim, "canvas")
}
