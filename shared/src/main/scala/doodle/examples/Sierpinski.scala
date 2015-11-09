package doodle.examples

import doodle.core._
import doodle.syntax._

object Sierpinski {
  def triangle(size: Double): Image = {
    println(s"Creating a triangle")
    Triangle(size, size) lineColor Color.magenta
  }

  def sierpinski(n: Int, size: Double): Image = {
    println(s"Creating a Sierpinski with n = $n")
    if(n == 1) {
      triangle(size)
    } else {
      val smaller = sierpinski(n - 1, size/2)
      smaller above (smaller beside smaller)
    }
  }

  val image = sierpinski(10, 512)
}
