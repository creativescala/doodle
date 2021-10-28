package doodle
package interact
package syntax

import cats.effect.IO
import doodle.algebra.Picture
import doodle.interact.algebra.MouseOver
import fs2.Stream

trait MouseOverSyntax {
  implicit class MouseOverOps[F[_], A](picture: F[A]) {
    def mouseOver(implicit m: MouseOver[F]): (F[A], Stream[IO, Unit]) =
      m.mouseOver(picture)
  }

  implicit class MouseOverPictureOps[Alg[x[_]] <: MouseOver[x], F[_], A](
      picture: Picture[Alg, F, A]
  ) {
    def mouseOver: IO[(Picture[Alg, F, A], Stream[IO, Unit])] = ???
  }
}
