package doodle
package interact
package algebra

import cats.effect.IO
import doodle.core.Point
import fs2.Pure
import fs2.Stream

/** Algebra for generating a stream of events giving the current mouse location.
  * Whenever the mouse moves a new event is generated. The algebra applies to a
  * Renderer's Canvas data type instead of the F data type, and hence gives the
  * mouse location in the canvas rather than relative to any Picture rendered on
  * the canvas.
  */
trait MouseMove[Canvas] {

  /** Return a stream that has an event every time the mouse moves across the
    * canvas. The coordinate system used is the global coordinate system used by
    * the Canvas, which usually means the origin is centered on the canvas.
    */
  def mouseMove(canvas: Canvas): Stream[IO, Point]
}
