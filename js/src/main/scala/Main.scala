import doodle.core._
import doodle.examples._
import doodle.syntax.image._
import doodle.js.HtmlCanvas

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

@JSExport object Main extends JSApp {
  @JSExport def main(): Unit = {
    import doodle.backend.StandardInterpreter._

    implicit val canvas = HtmlCanvas.fromElementId("canvas")
    Tree.image.draw
  }
}
