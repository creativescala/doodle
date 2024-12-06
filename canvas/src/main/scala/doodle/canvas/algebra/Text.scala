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
import doodle.core.BoundingBox
import doodle.core.Transform as Tx
import doodle.core.font.Font
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
    ): CanvasDrawing[Unit] = {
      CanvasDrawing { ctx =>
        ctx.save()

        tx(ctx)
        ctx.font = font.toString

        fill.foreach { f =>
          ctx.fillStyle = f.toCanvas
        }

        stroke.foreach { s =>
          ctx.strokeStyle = s.toCanvas
          ctx.lineWidth = s.width.toFloat
        }

        ctx.fillText(text, 0, 0)

        stroke.foreach { _ =>
          ctx.strokeText(text, 0, 0)
        }

        ctx.restore()
      }
    }

    def textBoundingBox(text: String, font: Font): (BoundingBox, Bounds) = {
      CanvasDrawing { ctx =>
        ctx.save()
        ctx.font = font.toString
        val textMetrics = ctx.measureText(text)

        val width = textMetrics.width
        val height = font.size.toDouble

        val boundingBox = BoundingBox(
          x = 0.0,
          y = 0.0,
          width = width,
          height = height
        )

        ctx.restore()

        (boundingBox, textMetrics)
      }
    }
  }
}
