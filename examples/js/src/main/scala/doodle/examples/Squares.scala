package doodle
package examples

object Squares {
  import cats.instances.all._
  import doodle.core._
  import doodle.syntax._
  import doodle.svg._

  val squares: Picture[Unit] = {
    val red = square[Algebra, Drawing](100).fillColor(Color.red)
    val green = square[Algebra, Drawing](100).fillColor(Color.green)
    val blue = square[Algebra, Drawing](100).fillColor(Color.blue)
    val yellow = square[Algebra, Drawing](100).fillColor(Color.yellow)

    red.beside(green).above(blue.beside(yellow))
  }
}
