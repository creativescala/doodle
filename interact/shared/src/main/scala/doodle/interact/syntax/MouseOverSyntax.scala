package doodle
package interact
package syntax

import cats.effect.IO
import doodle.algebra.Picture
import doodle.interact.algebra.MouseOver
import fs2.Stream

trait MouseOverSyntax {
  implicit class MouseOverPictureOps[Alg <: MouseOver, A](
      picture: Picture[Alg, A]
  ) {
    def mouseOver: IO[(Picture[Alg, A], Stream[IO, Unit])] = ???
  }
}
