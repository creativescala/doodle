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
package svg
package algebra

import doodle.algebra.generic.Finalized
import doodle.algebra.generic._
import doodle.core._
import doodle.core.font.Font
import doodle.core.{Transform => Tx}

import java.awt.geom.Rectangle2D
import scala.collection.mutable

trait TextModule extends JvmBase {
  trait Text extends GenericText[SvgResult] {
    self: HasTextBoundingBox[Rectangle2D] with Algebra {
      type Drawing[A] = Finalized[SvgResult, A]
    } =>
    val TextApi = new TextApi {
      type Bounds = Rectangle2D

      def textBoundingBox(
          text: String,
          font: Font
      ): (BoundingBox, Rectangle2D) =
        self.textBoundingBox(text, font)

      def text(
          tx: Tx,
          fill: Option[Fill],
          stroke: Option[Stroke],
          font: Font,
          text: String,
          bounds: Rectangle2D
      ): SvgResult[Unit] = {
        import bundle.implicits.{Tag => _, _}
        val set = mutable.Set.empty[Tag]
        // (0,0) of the Rectangle2D is the left baseline. For Doodle (0,0) is the
        // center of the bounding box.
        val style = Svg.toStyle(stroke, fill, set)
        val elt = Svg.textTag(text, font)(
          bundle.svgAttrs.x := -(bounds.getMinX() + bounds.getWidth()) / 2.0,
          bundle.svgAttrs.y := (bounds.getMinY() + bounds.getHeight()) / 2.0,
          bundle.svgAttrs.style := style
        )

        (elt, set, ())
      }
    }
  }
}
