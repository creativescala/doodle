package doodle
package svg
package algebra

import doodle.java2d.effect.Java2DFrame
import monix.reactive.Observable

trait Redraw extends doodle.interact.algebra.Redraw[Java2DFrame] {
  def redraw(canvas: Java2DFrame): Observable[Int] = {
    canvas.redraw
  }
}
