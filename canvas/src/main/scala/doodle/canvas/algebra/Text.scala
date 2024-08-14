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
import doodle.core.*
import doodle.core.BoundingBox
import doodle.core.font.Font
import doodle.core.{Transform as Tx}
import org.scalajs.dom

trait Text extends GenericText[CanvasDrawing] {
  self: Algebra { type Drawing[A] = Finalized[CanvasDrawing, A] } =>

  object TextApi extends TextApi {
    type Bounds = dom.TextMetrics

    def text(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        font: Font,
        text: String,
        bounds: Bounds
    ): CanvasDrawing[Unit] = ???
      // CanvasDrawing.setTransform(tx) >>
      //   CanvasDrawing.withFill(fill) {
      //     CanvasDrawing.withStroke(stroke) {
      //       CanvasDrawing.text(text, bounds.width, bounds.actualBoundingBoxLeft)
      //     }
      //   }

    def textBoundingBox(text: String, font: Font): (BoundingBox, Bounds) = {
      ???
    }
  }
}
