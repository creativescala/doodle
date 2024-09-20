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
import doodle.core.Transform as Tx

trait GenericShape[G[_]] extends Shape {
  self: Algebra { type Drawing[A] = Finalized[G, A] } =>

  trait ShapeApi {
    def rectangle(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        width: Double,
        height: Double
    ): G[Unit]
    def triangle(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        width: Double,
        height: Double
    ): G[Unit]
    def circle(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        diameter: Double
    ): G[Unit]
    def unit: G[Unit]
  }

  def ShapeApi: ShapeApi

  def rectangle(width: Double, height: Double): Finalized[G, Unit] =
    Finalized.leaf { dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = BoundingBox.centered(strokeWidth + width, strokeWidth + height)
      (
        bb,
        State.inspect(tx =>
          ShapeApi.rectangle(tx, dc.fill, dc.stroke, width, height)
        )
      )
    }

  def square(width: Double): Finalized[G, Unit] =
    rectangle(width, width)

  def triangle(width: Double, height: Double): Finalized[G, Unit] =
    Finalized.leaf { dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = BoundingBox.centered(strokeWidth + width, strokeWidth + height)

      (
        bb,
        State.inspect(tx =>
          ShapeApi.triangle(tx, dc.fill, dc.stroke, width, height)
        )
      )
    }

  def circle(diameter: Double): Finalized[G, Unit] =
    Finalized.leaf { dc =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb =
        BoundingBox.centered(strokeWidth + diameter, strokeWidth + diameter)
      (
        bb,
        State.inspect(tx => ShapeApi.circle(tx, dc.fill, dc.stroke, diameter))
      )
    }

  def empty: Finalized[G, Unit] =
    Finalized.leaf { _ =>
      (BoundingBox.empty, Renderable.unit(ShapeApi.unit))
    }
}
