/*
 * Copyright 2015 Noel Welsh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package interact
package algebra

import cats.effect.IO
import doodle.core.Point
import fs2.Stream

/** Algebra for generating a stream of events corresponding to mouse clicks.
  * Whenever the mouse is clicked a new event is generated with the location of
  * the click.
  *
  * This algebra applies to a Renderer's Canvas data types instead of the F data
  * type, and hence gives mouse click locations in the canvas rather than
  * relative to any Picture rendered on the Canvas.
  */
trait MouseClick[Canvas] {

  /** Return a stream that has an event every time the mouse is clicked on the
    * canvas. The coordinate system used is the global coordinate system used by
    * the Canvas, which usually means the origin is centered on the canvas.
    *
    * On systems, such as the browser, that will emulate touch events as mouse
    * events this will also return such touch events.
    */
  def mouseClick(canvas: Canvas): Stream[IO, Point]
}
