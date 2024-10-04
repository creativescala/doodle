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
package algebra
package generic

import cats.data.State
import doodle.core.BoundingBox
import doodle.core.{Transform as Tx}

trait GenericRaster[G[_], A] extends Raster[A] {
  self: Algebra { type Drawing[U] = Finalized[G, U] } =>

  trait RasterApi {
    def raster(
        tx: Tx,
        width: Int,
        height: Int
    )(f: A => Unit): G[Unit]
    def unit: G[Unit]
  }

  def RasterApi: RasterApi

  def raster(width: Int, height: Int)(f: A => Unit): Finalized[G, Unit] = {
    Finalized.leaf { dc =>
      val bb = BoundingBox.centered(width, height)
      (
        bb,
        State.inspect(tx => RasterApi.raster(tx, width, height)(f))
      )
    }
  }

  def empty: Finalized[G, Unit] =
    Finalized.leaf { _ =>
      (BoundingBox.empty, Renderable.unit(RasterApi.unit))
    }
}
