/*
 * Copyright 2015 Creative Scala
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
package svg
package algebra

import cats.effect.IO
import doodle.core.Point
import doodle.interact.algebra.MouseClick
import doodle.interact.algebra.MouseMove
import doodle.interact.algebra.Redraw
import doodle.svg.effect.Canvas
import fs2.Stream

trait CanvasAlgebra
    extends MouseClick[Canvas]
    with MouseMove[Canvas]
    with Redraw[Canvas] {

  def mouseClick(canvas: Canvas): Stream[IO, Point] =
    canvas.mouseClick

  def mouseMove(canvas: Canvas): Stream[IO, Point] =
    canvas.mouseMove

  def redraw(canvas: Canvas): Stream[IO, Int] =
    canvas.redraw
}
object CanvasAlgebra extends CanvasAlgebra
