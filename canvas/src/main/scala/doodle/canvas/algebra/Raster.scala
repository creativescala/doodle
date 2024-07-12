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
import org.scalajs.dom.CanvasRenderingContext2D
import doodle.core.Transform as Tx

trait Raster extends GenericRaster[CanvasDrawing, Immediate] {
  self: Algebra { type Drawing[U] = Finalized[CanvasDrawing, U] } =>

  object RasterApi extends RasterApi {
    def raster(
        tx: Tx,
        width: Int,
        height: Int
    )(f: Immediate => Unit): CanvasDrawing[Unit] = {
      CanvasDrawing.setTransform(tx) >>
        CanvasDrawing.raster(width, height)(f)
    }

    def unit: CanvasDrawing[Unit] = {
      CanvasDrawing.unit
    }
  }
}

