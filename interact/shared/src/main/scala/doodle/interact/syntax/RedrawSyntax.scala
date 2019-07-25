package doodle
package interact
package syntax

import doodle.interact.algebra.Redraw
import monix.reactive.Observable

trait RedrawSyntax {
  implicit class RedrawOps[Canvas](canvas: Canvas) {
    def redraw(implicit r: Redraw[Canvas]): Observable[Int] =
      r.redraw(canvas)
  }
}
