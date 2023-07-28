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

import doodle.algebra.generic._
import doodle.core.BoundingBox
import doodle.core.font.Font
import doodle.core.{Transform => Tx}
import org.scalajs.dom.svg.Rect
import scalatags.JsDom.svgAttrs

import scala.collection.mutable

trait TextModule extends JsBase {
  trait Text extends GenericText[SvgResult] {
    self: HasTextBoundingBox[Rect] with Algebra {
      type Drawing[A] = Finalized[SvgResult, A]
    } =>
    val TextApi = new TextApi {
      type Bounds = Rect

      def textBoundingBox(text: String, font: Font): (BoundingBox, Rect) =
        self.textBoundingBox(text, font)

      def text(
          tx: Tx,
          fill: Option[Fill],
          stroke: Option[Stroke],
          font: Font,
          text: String,
          bounds: Rect
      ): SvgResult[Unit] = {
        import bundle.implicits.{Tag => _, _}
        val set = mutable.Set.empty[Tag]
        // (0,0) of the bounds Rect is the left baseline. See
        // https://www.w3.org/Graphics/SVG/IG/resources/svgprimer.html#getBBox
        //
        // For Doodle (0,0) is the center of the bounding box.
        val style = Svg.toStyle(stroke, fill, set)
        val elt = Svg.textTag(text, font)(
          // Our y coordinates are reversed (y increases as we move up in
          // Doodle, and decreases in SVG). The transform takes care of this but
          // means the text will be drawn upside down, so we must flip it before
          // rendering
          svgAttrs.transform := Svg.toSvgTransform(
            Tx.verticalReflection.andThen(tx)
          ),
          svgAttrs.x := -(bounds.x + bounds.width) / 2.0,
          svgAttrs.y := (bounds.y + bounds.height) / 2.0,
          // Setting baseline to the middle allows us to accurately layout the
          // text. Otherwise we don't know how far the baseline is offset from
          // the bounding box we're given.
          svgAttrs.dominantBaseline := "middle",
          svgAttrs.style := style
        )

        (elt, set, ())
      }
    }
  }
}
