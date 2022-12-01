package docs
package algebra

import cats.effect.unsafe.implicits.global
import cats.implicits._
import doodle.core._
import doodle.java2d._
import doodle.syntax.all._ // For Color

object CircleSquare {
  val smallCircle = Picture.circle(100)
  val largeSquare = Picture.square(200)

  val composition =
    smallCircle
      .fillColor(Color.crimson)
      .noStroke
      .on(largeSquare.fillColor(Color.midnightBlue).strokeWidth(5))

  composition.save("algebra/circle-square.png")
}
