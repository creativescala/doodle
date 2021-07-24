package doodle
package interact
package algebra

import monix.reactive.Observable

/** Algebra for generating a stream of events indicating when the canvas is
  * ready to redraw. The algebra applies to a Renderer's Canvas data type
  * instead of the F data type.
  */
trait Redraw[Canvas] {

  /** Return an Observable that has an event every time the canvas is ready to
    * redraw. The value is the approximate time in millisecond since a frame was
    * last rendered.
    */
  def redraw(canvas: Canvas): Observable[Int]
}
