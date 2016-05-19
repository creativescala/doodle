import doodle.core._
import doodle.examples._
import doodle.syntax.image._
import doodle.js.SvgCanvas

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

@JSExport object Main extends JSApp {
  @JSExport def main(): Unit = {
    import doodle.backend.StandardInterpreter._

    implicit val canvas = SvgCanvas.fromElementId("canvas", 400, 400)
    //Tree.image.draw
    ChristmasTree.image.draw
  }
}
