import doodle.core._
import doodle.examples._

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

@JSExport object Main extends JSApp {
  val image = ChristmasTree
  val anim  = Orbit(Angle.radians(0), 50, image)

  @JSExport def main(): Unit =
    doodle.js.animate(anim, "canvas")
}
