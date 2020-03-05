package doodle
package examples

object GradientSquares {
  import cats.instances.all._
  import doodle.core._
  import doodle.syntax._
  import doodle.svg._

  val width = 100.0
  val gradient = Gradient.dichromaticVertical(Color.red, Color.blue, width)
  val gradientSquare =
    square[Algebra,Drawing](width)
      .fillGradient(gradient)

  val image: Picture[Unit] =
    gradientSquare.above(gradientSquare)
}
