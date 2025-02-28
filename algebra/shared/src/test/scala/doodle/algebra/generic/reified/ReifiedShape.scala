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
package reified

import cats.data.WriterT
import doodle.core.Point
import doodle.core.Transform as Tx

trait ReifiedShape extends GenericShape[Reification] {
  self: Algebra { type Drawing[A] = TestAlgebra.Drawing[A] } =>

  object ShapeApi extends ShapeApi {
    def append(a: Option[Reified], b: Option[Reified]): Reification[Unit] =
      WriterT.tell(a.toList ++ b.toList)

    def rectangle(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        width: Double,
        height: Double
    ): Reification[Unit] =
      append(
        fill.map(f => Reified.fillRect(tx, f, width, height)),
        stroke.map(s => Reified.strokeRect(tx, s, width, height))
      )

    def triangle(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        width: Double,
        height: Double
    ): Reification[Unit] = {
      val w = width / 2.0
      val h = height / 2.0
      val points = Array(Point(-w, -h), Point(0, h), Point(w, -h))
      append(
        fill.map(f => Reified.fillPolygon(tx, f, points)),
        stroke.map(s => Reified.strokePolygon(tx, s, points))
      )
    }

    def circle(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        diameter: Double
    ): Reification[Unit] =
      append(
        fill.map(f => Reified.fillCircle(tx, f, diameter)),
        stroke.map(s => Reified.strokeCircle(tx, s, diameter))
      )

    def unit: Reification[Unit] =
      WriterT.tell(List.empty[Reified])
  }
}
