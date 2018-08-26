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
    Image.circle(size(n))

  def square(n: Int): Image =
    Image.rectangle(2*size(n), 2*size(n))

  def triangle(n: Int): Image =
    Image.triangle(2*size(n), 2*size(n))

  def colored(shape: Int => Image, color: Int => Color): Int => Image =
    (n: Int) =>
      shape(n) lineWidth 10 lineColor color(n)

  def concentricShapes(count: Int, singleShape: Int => Image): Image =
    count match {
      case 0 => Image.empty
      case n => singleShape(n) on concentricShapes(n-1, singleShape)
    }

  val spacer = Image.square(10).noFill.noLine

  def image =
    concentricShapes(10, colored(circle, spinning)) beside
    spacer beside
    concentricShapes(10, colored(triangle, fading)) beside
    spacer beside
    concentricShapes(10, colored(square, spinning))
}
