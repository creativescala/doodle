package doodle
package svg
package algebra

import cats.Semigroup
import doodle.language.Basic
import doodle.algebra.Layout
import doodle.algebra.generic._
import doodle.algebra.generic.reified._

final case object Algebra
  extends Layout[Finalized[Reification,?]]
  with ReifiedPath
  with ReifiedShape
  with GenericStyle[Reification]
  with GenericTransform[Reification]
  with Basic[Drawing]
{
    val layout = ReifiedLayout.instance

    def on[A](top: Finalized[Reification,A], bottom: Finalized[Reification,A])(implicit s: Semigroup[A]): Finalized[Reification,A] =
        layout.on(top, bottom)(s)

    def beside[A](left: Finalized[Reification,A], right: Finalized[Reification,A])(implicit s: Semigroup[A]): Finalized[Reification,A] =
        layout.beside(left, right)(s)

    def above[A](top: Finalized[Reification,A], bottom: Finalized[Reification,A])(implicit s: Semigroup[A]): Finalized[Reification,A] =
        layout.above(top, bottom)(s)

    def at[A](img: Finalized[Reification,A], x: Double, y: Double): Finalized[Reification,A] =
        layout.at(img, x, y)
}
