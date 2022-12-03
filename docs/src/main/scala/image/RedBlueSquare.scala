package docs
package image

import doodle.image._
import doodle.core._
import cats.effect.unsafe.implicits.global

object RedBlueSquare {
  val redSquare = Image.square(100).fillColor(Color.red)
  val blueSquare = Image.square(100).fillColor(Color.blue)
  val combination = redSquare.beside(blueSquare)

  combination.save("image/red-blue.png")
}
