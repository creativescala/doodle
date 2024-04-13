package docs
package pictures

import doodle.core.*
import doodle.image.*
import doodle.image.syntax.all.*
import doodle.java2d.*
import cats.effect.unsafe.implicits.global

object ImageObj {
    val redSquare = Image.square(100).fillColor(Color.red)
    val blueSquare = Image.square(100).fillColor(Color.blue)

    val composition = redSquare.beside(blueSquare)

    composition.save("pictures/red-blue.png")

}
