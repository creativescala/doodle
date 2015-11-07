package doodle.examples

import doodle.core._
import doodle.syntax._

object ConcentricCircles {
  def singleCircle(n: Int): Image =
    Circle(50 + 5 * n) lineColor (Color.red fadeOut (n / 20).normalized)

  def concentricCircles(n: Int): Image =
    if(n == 1) {
      singleCircle(n)
    } else {
      singleCircle(n) on concentricCircles(n - 1)
    }

  def image: Image =
    concentricCircles(20)
}
