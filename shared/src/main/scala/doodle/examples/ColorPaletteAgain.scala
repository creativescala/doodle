package doodle.examples

import doodle.core._
import doodle.syntax._

object ColorPaletteAgain extends Drawable {

  val circleMinimum = 50
  val circleIncrement = 10

  def complement(c: Color): Color =
    c.spin(180.degrees)

  def analogous(c: Color): Color =
    c.spin(15.degrees)

  def singleCircle(n: Int, color: Color): Image =
    Circle(circleMinimum + circleIncrement * n) lineColor color lineWidth circleIncrement

  def complementCircles(n: Int, c: Color): Image = {
    val color = complement(c)
    if(n == 1) {
      singleCircle(n, color)
    } else {
      complementCircles(n - 1, color) on singleCircle(n, color)
    }
  }
  def draw =
    complementCircles(10, Color.seaGreen)
}
