package doodle
package examples

import cats.implicits._
import doodle.algebra.{Picture, Text}
import doodle.language.Basic
import doodle.syntax._

/**
 * Really basic example of a picture that abstracts over algebras, and hence is
 * independent of back-end.
 */
object Letters {
  def letters[Alg[x[_]] <: Basic[x] with Text[x], F[_]]: Picture[Alg, F, Unit] =
    text[Alg,F]("Hi from Doodle!").on(
      square[Alg,F](40)
    )
}
