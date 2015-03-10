import doodle.core._
import doodle.examples._

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

@JSExport object Main extends JSApp {
  @JSExport def main(): Unit =
    doodle.js.draw(Tree, "canvas")
}
