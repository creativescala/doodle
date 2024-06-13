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
import doodle.core.{Transform as Tx}

trait GenericText[G[_]] extends Text {
  self: Algebra { type Drawing[A] = Finalized[G, A] } =>

  trait TextApi {

    /** The type of additional information that is useful for laying out text.
      *
      * Text layout is complicated. Doodle's layout only cares about the
      * bounding box, with the usual assumption that the origin is the center of
      * the bounding box. However, when we come to actually render the text we
      * usually want additional information. In particular we usually specify
      * the origin where we start rendering as the left-most point on the
      * baseline of the text (and text may descend below the baseline). This is
      * difficult to calculate just from the Doodle bounding box, so we allow
      * methods to return an additional piece of information that can be used to
      * layout the text.
      */
    type Bounds

    def text(
        tx: Tx,
        fill: Option[Fill],
        stroke: Option[Stroke],
        font: Font,
        text: String,
        bounds: Bounds
    ): G[Unit]

    def textBoundingBox(text: String, font: Font): (BoundingBox, Bounds)
  }

  def TextApi: TextApi

  def font[A](image: Finalized[G, A], font: Font): Finalized[G, A] =
    Finalized.contextTransform(_.font(font))(image)

  def text(text: String): Finalized[G, Unit] = {
    val api = TextApi

    Finalized.leaf { dc =>
      val (bb, bounds) = api.textBoundingBox(text, dc.font)
      (
        bb,
        State.inspect(tx =>
          api.text(tx, dc.fill, dc.stroke, dc.font, text, bounds)
        )
      )
    }
  }
}
