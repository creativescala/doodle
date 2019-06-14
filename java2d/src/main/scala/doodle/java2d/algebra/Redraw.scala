package doodle
package svg
package algebra

import doodle.java2d.effect.Canvas
import monix.reactive.Observable

trait Redraw extends doodle.interact.algebra.Redraw[Canvas] {
  def redraw(canvas: Canvas): Observable[Int] = {
    canvas.redraw
  }
}
