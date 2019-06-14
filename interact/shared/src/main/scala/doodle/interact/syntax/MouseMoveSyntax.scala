package doodle
package interact
package syntax

import doodle.core.Point
import doodle.interact.algebra.MouseMove
import monix.reactive.Observable

trait MouseMoveSyntax {
  implicit class MouseMoveOps[Canvas](canvas: Canvas) {
    def mouseMove(implicit m: MouseMove[Canvas]): Observable[Point] =
      m.mouseMove(canvas)
  }
}
