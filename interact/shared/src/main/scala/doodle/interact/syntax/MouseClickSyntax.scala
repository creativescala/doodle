package doodle
package interact
package syntax

import cats.effect.IO
import doodle.core.Point
import doodle.interact.algebra.MouseClick
import fs2.Stream

trait MouseClickSyntax {
  implicit class MouseClickOps[Canvas](canvas: Canvas) {
    def mouseClick(implicit m: MouseClick[Canvas]): Stream[IO, Point] =
      m.mouseClick(canvas)
  }
}
