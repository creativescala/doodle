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
