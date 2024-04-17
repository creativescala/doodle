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
import doodle.core.font.Font
import doodle.core.{Transform => Tx}

trait GenericClipIt[G[_]] extends ClipIt {
  self: Algebra { type Drawing[A] = Finalized[G, A] } =>

  trait ClipApi {
    type Bounds

    def clipit(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        font: Font,
        text: String,
        bounds: Bounds
    ): G[Unit]
    def textBoundingBox(text: String, font: Font): (BoundingBox, Bounds)
  }

  def ClipApi: ClipApi

  def cfont[A](image: Finalized[G, A], font: Font): Finalized[G, A] =
    Finalized.contextTransform(_.font(font))(image)

  def clipit(text: String): Finalized[G, Unit] = {
    val api = ClipApi

    Finalized.leaf { dc =>
      val (bb, bounds) = api.textBoundingBox(text, dc.font)
      (
        bb,
        State.inspect(tx =>
          api.clipit(tx, dc.fill, dc.stroke, dc.font, text, bounds)
        )
      )
    }
  }
}
