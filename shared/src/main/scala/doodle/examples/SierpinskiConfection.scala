package doodle
package examples

import doodle.core._
import doodle.random._
import doodle.syntax._

object SierpinskiConfection {
  val reddish: Random[Color] = {
    val hue = Random.double map { d => (d - 0.5) * 0.2 }
    val saturation = Random.double map { s => s * 0.3 + 0.4 }
    val lightness = Random.double map { l => l * 0.3 + 0.3 }

    for {
      h <- hue
      s <- saturation
      l <- lightness
    } yield HSLA(h.turns, s.normalized, l.normalized, 1.normalized)
  }

  def triangle(size: Double): Image = {
    Image.triangle(size, size)
  }
  def circle(size: Double): Image = {
    Image.circle(size / 2)
  }

  def shape(size: Double): Random[Image] = {
    for {
      s <- Random.oneOf(triangle(size), circle(size))
      h <- reddish
    } yield s fillColor h
  }

  def sierpinski(n: Int, size: Double): Random[Image] = {
    if(n == 1) {
      shape(size)
    } else {
      val smaller = sierpinski(n - 1, size/2)
      for {
        a <- smaller
        b <- smaller
        c <- smaller
      } yield a above (b beside c)
    }
  }

  val image = sierpinski(5, 512).run
}
