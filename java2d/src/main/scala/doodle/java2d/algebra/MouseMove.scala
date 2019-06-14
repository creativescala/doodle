package doodle
package svg
package algebra

import doodle.core.Point
import doodle.java2d.effect.Java2DFrame
import monix.reactive.Observable

trait MouseMove extends doodle.interact.algebra.MouseMove[Java2DFrame] {
  def mouseMove(canvas: Java2DFrame): Observable[Point] = {
    canvas.mouseMove
  }
}
