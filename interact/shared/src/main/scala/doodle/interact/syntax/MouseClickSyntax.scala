package doodle
package interact
package syntax

import doodle.core.Point
import doodle.interact.algebra.MouseClick
import monix.reactive.Observable

trait MouseClickSyntax {
  implicit class MouseClickOps[Canvas](canvas: Canvas) {
    def mouseClick(implicit m: MouseClick[Canvas]): Observable[Point] =
      m.mouseClick(canvas)
  }
}
