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
import doodle.core.{Transform => Tx}

trait GenericShape[F[_]] extends Shape[Finalized[F, ?]] {

  trait ShapeApi {
    def rectangle(tx: Tx,
                  fill: Option[Fill],
                  stroke: Option[Stroke],
                  width: Double,
                  height: Double): F[Unit]
    def triangle(tx: Tx,
                 fill: Option[Fill],
                 stroke: Option[Stroke],
                 width: Double,
                 height: Double): F[Unit]
    def circle(tx: Tx,
               fill: Option[Fill],
               stroke: Option[Stroke],
               diameter: Double): F[Unit]
    def unit: F[Unit]
  }

  def ShapeApi: ShapeApi

  def rectangle(width: Double, height: Double): Finalized[F, Unit] =
    Finalized.leaf { dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = BoundingBox.centered(strokeWidth + width, strokeWidth + height)
      (bb,
       State.inspect(tx =>
         ShapeApi.rectangle(tx, dc.fill, dc.stroke, width, height)))
    }

  def square(width: Double): Finalized[F, Unit] =
    rectangle(width, width)

  def triangle(width: Double, height: Double): Finalized[F, Unit] =
    Finalized.leaf { dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = BoundingBox.centered(strokeWidth + width, strokeWidth + height)

      (bb,
       State.inspect(tx =>
         ShapeApi.triangle(tx, dc.fill, dc.stroke, width, height)))
    }

  def circle(diameter: Double): Finalized[F, Unit] =
    Finalized.leaf { dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb =
        BoundingBox.centered(strokeWidth + diameter, strokeWidth + diameter)
      (bb,
       State.inspect(tx => ShapeApi.circle(tx, dc.fill, dc.stroke, diameter)))
    }

  def empty: Finalized[F, Unit] =
    Finalized.leaf { _ =>
      (BoundingBox.empty, Renderable.unit(ShapeApi.unit))
    }
}
