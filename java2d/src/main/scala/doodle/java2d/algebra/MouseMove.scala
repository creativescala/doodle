package doodle
package java2d
package algebra

import doodle.core.Point
import doodle.java2d.effect.Canvas
import monix.reactive.Observable

trait MouseMove extends doodle.interact.algebra.MouseMove[Canvas] {
  def mouseMove(canvas: Canvas): Observable[Point] = {
    canvas.mouseMove
  }
}
