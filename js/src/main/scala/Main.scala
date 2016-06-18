import doodle.examples._
import doodle.syntax.image._
import doodle.js.SvgCanvas

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

@JSExport object Main extends JSApp {
  @JSExport def main(): Unit = {
    import doodle.backend.StandardInterpreter._

    implicit val canvas = SvgCanvas.fromElementId("canvas", 600, 600)
    Tree.image.draw
    //ChristmasTree.image.draw
  }
}
