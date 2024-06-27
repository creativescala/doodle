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

package doodle.canvas.algebra

import doodle.algebra.Algebra
import doodle.algebra.generic.*
import doodle.core.ClosedPath
import doodle.core.Point
import doodle.core.{Transform as Tx}

trait Shape extends GenericShape[CanvasDrawing] {
  self: Algebra { type Drawing[A] = Finalized[CanvasDrawing, A] } =>

  object ShapeApi extends ShapeApi {
    def rectangle(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        width: Double,
        height: Double
    ): CanvasDrawing[Unit] =
      CanvasDrawing.setTransform(tx) >>
        CanvasDrawing.withStroke(stroke) {
          CanvasDrawing.withFill(fill) {
            CanvasDrawing.rectangle(width, height)
          }
        }

    def triangle(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        width: Double,
        height: Double
    ): CanvasDrawing[Unit] =
      CanvasDrawing.setTransform(tx) >>
        CanvasDrawing.withStroke(stroke) {
          CanvasDrawing.withFill(fill) {
            CanvasDrawing.closedPath(ClosedPath.triangle(width, height))
          }
        }

    def circle(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        diameter: Double
    ): CanvasDrawing[Unit] =
      CanvasDrawing.setTransform(tx) >>
        CanvasDrawing.withStroke(stroke) {
          CanvasDrawing.withFill(fill) {
            CanvasDrawing.closedPath(ClosedPath.circle(Point.zero, diameter))
          }
        }

    def unit: CanvasDrawing[Unit] =
      CanvasDrawing.unit
  }
}
