import doodle.examples._
import doodle.syntax.image._
import doodle.js.SvgFrame

object Main {
  def main(args: Array[String]): Unit = {
    import doodle.backend.StandardInterpreter._

    implicit val frame = SvgFrame.fromElementId("canvas", 600, 600)
    Tree.image.draw
    //ChristmasTree.image.draw
  }
}
