/*
 * Copyright 2015-2020 Noel Welsh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OReification ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package java2d
package algebra

import doodle.core.Point
import doodle.interact.algebra.MouseClick
import doodle.interact.algebra.MouseMove
import doodle.interact.algebra.Redraw
import doodle.java2d.effect.Canvas
import monix.reactive.Observable

case object CanvasAlgebra
    extends MouseClick[Canvas]
    with MouseMove[Canvas]
    with Redraw[Canvas] {
  def mouseClick(canvas: Canvas): Observable[Point] = {
    canvas.mouseClick
  }

  def mouseMove(canvas: Canvas): Observable[Point] = {
    canvas.mouseMove
  }

  def redraw(canvas: Canvas): Observable[Int] = {
    canvas.redraw
  }
}
