package doodle
package svg
package algebra

import doodle.svg.effect.Canvas
import monix.reactive.Observable

object Redraw extends doodle.interact.algebra.Redraw[Canvas] {
  def redraw(canvas: Canvas): Observable[Int] = {
    canvas.redraw
  }
}
