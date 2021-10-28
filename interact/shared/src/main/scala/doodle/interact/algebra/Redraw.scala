package doodle
package interact
package algebra

import cats.effect.IO
import fs2.Stream

/** Algebra for generating a stream of events indicating when the canvas is
  * ready to redraw. The algebra applies to a Renderer's Canvas data type
  * instead of the F data type.
  */
trait Redraw[Canvas] {

  /** Return a stream that has an event every time the canvas is ready to
    * redraw. The value is the approximate time in millisecond since a frame was
    * last rendered.
    */
  def redraw(canvas: Canvas): Stream[IO, Int]
}
