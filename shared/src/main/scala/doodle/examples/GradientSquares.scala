package doodle.examples

import doodle.core._

object GradientSquares {

  val width = 100.0

  val grad = Gradient.dichromaticVertical(Color.red, Color.blue, width)

  val gradientSquare = Image.square(width) fillGradient grad

  val image = gradientSquare above gradientSquare
}
