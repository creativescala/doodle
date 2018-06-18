import doodle.examples._
import doodle.syntax.image._
import doodle.js.SvgFrame

import scala.scalajs.js.annotation._

object Main {
  @JSExportTopLevel("Main.main")
  def main(): Unit = {
    import doodle.backend.StandardInterpreter._

    implicit val frame = SvgFrame.fromElementId("canvas", 600, 600)
    Tree.image.draw
    //ChristmasTree.image.draw
  }
}
