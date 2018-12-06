package doodle.examples

import doodle.core._
import doodle.image.Image
import doodle.syntax._

object ConcentricCircles {
  def fade(n: Int): Image =
    singleCircle(n).strokeColor(Color.red fadeOut (n / 20.0).normalized)

  def gradient(n: Int): Image =
    singleCircle(n).strokeColor(Color.royalBlue.spin((n * 15).degrees))

  def singleCircle(n: Int): Image =
    Image.circle(50.0 + 7 * n).strokeWidth(3.0)

  def concentricCircles(n: Int): Image =
    n match {
      case 0 => singleCircle(n)
      case n => singleCircle(n) on concentricCircles(n - 1)
    }

  def fadeCircles(n: Int): Image =
    n match {
      case 0 => fade(n)
      case n => fade(n) on fadeCircles(n - 1)
    }

  def gradientCircles(n: Int): Image =
    n match {
      case 0 => gradient(n)
      case n => gradient(n) on gradientCircles(n - 1)
    }

  def image: Image =
    concentricCircles(20).strokeColor(Color.royalBlue)

  def fade: Image =
    fadeCircles(20)

  def gradient: Image =
    gradientCircles(20)
}
