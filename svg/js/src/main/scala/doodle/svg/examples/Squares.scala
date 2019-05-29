package doodle
package svg
package examples

object Squares {
  import cats.instances.all._
  import doodle.core._
  import doodle.syntax._
  import doodle.language.Basic
  import doodle.svg._
  import doodle.interact.syntax._
  import scala.scalajs.js.annotation._

  val squares =
    Basic.picture[Drawing, Unit]{ implicit algebra: Basic[Drawing] =>
      import algebra._

      val red = square(100).fillColor(Color.red)
      val green = square(100).fillColor(Color.green)
      val blue = square(100).fillColor(Color.blue)
      val yellow = square(100).fillColor(Color.yellow)

      red.beside(green).above(blue.beside(yellow))
    }

  val frame = Frame("canvas")

  @JSExportTopLevel("Main")
  def main(): Unit = {
    squares.draw(frame)
  }
}
