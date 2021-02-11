package doodle
package svg
package algebra

import doodle.interact.algebra.{MouseClick, MouseMove, Redraw}
import doodle.core.Point
import doodle.svg.effect.Canvas
import monix.reactive.Observable

trait CanvasAlgebra
    extends MouseClick[Canvas]
    with MouseMove[Canvas]
    with Redraw[Canvas] {

  def mouseClick(canvas: Canvas): Observable[Point] =
    canvas.mouseClick

  def mouseMove(canvas: Canvas): Observable[Point] =
    canvas.mouseMove

  def redraw(canvas: Canvas): Observable[Int] =
    canvas.redraw
}
object CanvasAlgebra extends CanvasAlgebra
