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

import cats.Eval
import doodle.algebra.generic.BlendMode
import doodle.algebra.generic.Finalized
import doodle.algebra.generic.GenericBlend
import doodle.algebra.generic.Renderable

trait CanvasBlend extends GenericBlend[CanvasDrawing] {
  self: doodle.algebra.Algebra {
    type Drawing[A] = Finalized[CanvasDrawing, A]
  } =>

  object BlendApi extends BlendApi {
    def applyBlend[A](
        image: Finalized[CanvasDrawing, A],
        blendMode: BlendMode
    ): Finalized[CanvasDrawing, A] = {
      // The Canvas 2D API uses "source-over" where CSS uses "normal"
      val mode = blendMode match {
        case BlendMode.Normal => "source-over"
        case other            => other.toCssName
      }
      Finalized { transform =>
        image.run(transform).map { case (bb, rdr) =>
          val newRenderable: Renderable[CanvasDrawing, A] =
            Renderable { tx =>
              val original: Eval[CanvasDrawing[A]] = rdr.runA(tx)

              original.map { drawing =>
                CanvasDrawing.withCompositeOperation(mode)(drawing)
              }
            }
          (bb, newRenderable)
        }
      }
    }
  }
}
