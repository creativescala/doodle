package docs
package algebra

import cats.effect.unsafe.implicits.global
import cats.implicits._
import doodle.algebra.Picture
import doodle.algebra.Text
import doodle.core._
import doodle.java2d._
import doodle.language.Basic
import doodle.syntax.all._

object BasicWithText {

  def basicWithText[Alg <: Basic]: Picture[Alg, Unit] = {
    val redCircle = circle[Alg](100).strokeColor(Color.red)
    val rad = text[Alg]("Doodle is rad")

    rad.on(redCircle)
  }
  basicWithText[Algebra].save("algebra/basic-with-text.png")
}
