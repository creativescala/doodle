package doodle
package image
package examples

import doodle.core._
import doodle.image.Image

object Sierpinski {
  def triangle(size: Double): Image = {
    println(s"Creating a triangle")
    // This is just a suggestion.
    // https://en.wikipedia.org/wiki/Sierpi%C5%84ski_triangle says that the triangles are 
    // equilateral: "The Sierpiński triangle ...is a fractal attractive fixed set with the 
    // overall shape of an equilateral triangle, subdivided recursively into smaller 
    // equilateral triangles".
    // By passing in width=size and height=size, we are creating an isosceles triangle. 
    // If desired, we can instead draw an equilateral triangle by passing in height=√3÷2×size.
    // See https://www.slideshare.net/pjschwarz/sierpinski-triangle-polyglot-fp-for-fun-and-profit-haskell-and-scala-with-minor-corrections#49
    Image.triangle(size, size).strokeColor(Color.magenta)
  }

  def sierpinski(n: Int, size: Double): Image = {
    println(s"Creating a Sierpinski with n = $n")
    if (n == 1) {
      triangle(size)
    } else {
      val smaller = sierpinski(n - 1, size / 2)
      smaller above (smaller beside smaller)
    }
  }

  val image = sierpinski(10, 512)
}
