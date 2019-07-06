package doodle
package interact
package algebra

import doodle.core.Point
import monix.reactive.Observable

/** Algebra for generating a stream of events giving the current mouse location.
  * Whenever the mouse moves a new event is generated. The algebra applies to a
  * Renderer's Canvas data type instead of the F data type, and hence gives the
  * mouse location in the canvas rather than relative to any Picture rendered on
  * the canvas. */
trait MouseMove[Canvas] {

  /** Return an Observable that has an event every time the mouse moves across the
    * canvas. The coordinate system used is the global coordinate system used by
    * the Canvas, which usually means the origin is centered on the canvas. */
  def mouseMove(canvas: Canvas): Observable[Point]
}
