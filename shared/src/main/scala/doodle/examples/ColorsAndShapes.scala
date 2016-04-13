package doodle.examples

import doodle.core._
import doodle.syntax._

object ColorsAndShapes {
  def size(n: Int): Double =
    50 + 12 * n

  def fading(n: Int): Color =
    Color.blue fadeOut (1 - n / 20.0).normalized

  def spinning(n: Int): Color =
    Color.blue desaturate 0.5.normalized spin (n * 30).degrees

  def circle(n: Int): Image =
    Circle(size(n))

  def square(n: Int): Image =
    Rectangle(2*size(n), 2*size(n))

  def triangle(n: Int): Image =
    Triangle(2*size(n), 2*size(n))

  def colored(shape: Int => Image, color: Int => Color): Int => Image =
    (n: Int) =>
      shape(n) lineWidth 10 lineColor color(n)

  def manyShapes(n: Int, singleShape: Int => Image): Image =
    if(n == 1) {
      singleShape(n)
    } else {
      singleShape(n) on manyShapes(n - 1, singleShape)
    }

  def image =
    manyShapes(10, colored(circle, spinning)) beside
    manyShapes(10, colored(triangle, fading)) beside
    manyShapes(10, colored(square, spinning))
}
