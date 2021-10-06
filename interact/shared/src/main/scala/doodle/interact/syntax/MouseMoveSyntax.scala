package doodle
package interact
package syntax

import cats.effect.IO
import doodle.core.Point
import doodle.interact.algebra.MouseMove
import fs2.Stream

trait MouseMoveSyntax {
  implicit class MouseMoveOps[Canvas](canvas: Canvas) {
    def mouseMove(implicit m: MouseMove[Canvas]): Stream[IO, Point] =
      m.mouseMove(canvas)
  }
}
