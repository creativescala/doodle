package docs
package algebra

import cats.effect.unsafe.implicits.global
import doodle.core._
import doodle.language.Basic
import doodle.syntax.all._

object BasicWithText {

  val redCircle = circle[Basic](100).strokeColor(Color.red)
  val rad = text[Basic]("Doodle is rad")

  val picture = rad.on(redCircle)
  picture.save("algebra/basic-with-text.png")
}
