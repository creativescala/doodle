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

import cats.data.State
import doodle.algebra.generic.*
import doodle.core.BoundingBox
import doodle.core.Transform as Tx

import scala.collection.mutable

/** Module for handling bitmap images in SVG via <image> elements */
trait ImageModule { root: Base with SvgModule =>
  trait Image {
    self: doodle.algebra.Algebra {
      type Drawing[A] = doodle.algebra.generic.Finalized[SvgResult, A]
    } =>

    /** Create an SVG image element from a reference */
    def image(ref: SvgImageRef): Drawing[Unit] = {
      Finalized.leaf { dc =>
        val bb = (ref.width, ref.height) match {
          case (Some(w), Some(h)) => BoundingBox.centered(w, h)
          case (Some(w), None)    => BoundingBox.centered(w, w)
          case (None, Some(h))    => BoundingBox.centered(h, h)
          case (None, None)       => BoundingBox.centered(100, 100)
        }

        val renderable = State.inspect[Tx, SvgResult[Unit]] { tx =>
          val b = bundle
          import b.implicits.*
          import b.{svgAttrs, svgTags}

          val set = mutable.Set.empty[root.Tag]

          val imgTag = (ref.width, ref.height) match {
            case (Some(w), Some(h)) =>
              svgTags.image(
                svgAttrs.xLinkHref := ref.href,
                svgAttrs.width := w,
                svgAttrs.height := h,
                svgAttrs.x := -w / 2,
                svgAttrs.y := -h / 2,
                svgAttrs.transform := Svg.toSvgTransform(tx)
              )
            case (Some(w), None) =>
              svgTags.image(
                svgAttrs.xLinkHref := ref.href,
                svgAttrs.width := w,
                svgAttrs.x := -w / 2,
                svgAttrs.transform := Svg.toSvgTransform(tx)
              )
            case (None, Some(h)) =>
              svgTags.image(
                svgAttrs.xLinkHref := ref.href,
                svgAttrs.height := h,
                svgAttrs.y := -h / 2,
                svgAttrs.transform := Svg.toSvgTransform(tx)
              )
            case (None, None) =>
              svgTags.image(
                svgAttrs.xLinkHref := ref.href,
                svgAttrs.transform := Svg.toSvgTransform(tx)
              )
          }

          (imgTag, set, ())
        }

        (bb, renderable)
      }
    }
  }
}
