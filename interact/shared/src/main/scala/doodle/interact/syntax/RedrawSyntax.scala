package doodle
package interact
package syntax

import cats.effect.IO
import doodle.interact.algebra.Redraw
import fs2.Stream

trait RedrawSyntax {
  implicit class RedrawOps[Canvas](canvas: Canvas) {
    def redraw(implicit r: Redraw[Canvas]): Stream[IO, Int] =
      r.redraw(canvas)
  }
}
