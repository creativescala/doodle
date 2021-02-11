package docs
package algebra

import cats.implicits._
import doodle.java2d._
import doodle.syntax._
import doodle.core._
import doodle.language.Basic
import doodle.algebra.{Picture, Text}

object BasicWithText {
  def basicWithText[Alg[x[_]] <: Basic[x] with Text[x], F[_]]
    : Picture[Alg, F, Unit] = {
    val redCircle = circle[Alg, F](100).strokeColor(Color.red)
    val rad = text[Alg, F]("Doodle is rad")

    rad.on(redCircle)
  }
  basicWithText[Algebra, Drawing].save("algebra/basic-with-text.png")
}
