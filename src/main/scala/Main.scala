import doodle.core._
import doodle.syntax.angle._
import doodle.syntax.normalised._

// object Java2DMain extends App with Main {
//   doodle.java2d.Draw(picture)
// }

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

@JSExport object Main extends JSApp with doodle.Example {
  @JSExport def main(): Unit =
    doodle.canvas.Draw(picture, "canvas")
}
