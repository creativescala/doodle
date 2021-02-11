package doodle
package interact
package algebra

import doodle.core.Point
import monix.reactive.Observable

/**
  * Algebra for generating a stream of events corresponding to mouse clicks.
  * Whenever the mouse is clicked a new event is generated with the location of
  * the click.
  *
  * This algebra applies to a Renderer's Canvas data types instead of the F data
  * type, and hence gives mouse click locations in the canvas rather than
  * relative to any Picture rendered on the Canvas.
  */
trait MouseClick[Canvas] {

  /**
    * Return an Observable that has an event every time the mouse is clicked on
    * the canvas. The coordinate system used is the global coordinate system
    * used by the Canvas, which usually means the origin is centered on the
    * canvas.
    *
    * On systems, such as the browser, that will emulate touch events as mouse
    * events this will also return such touch events.
    */
  def mouseClick(canvas: Canvas): Observable[Point]
}
