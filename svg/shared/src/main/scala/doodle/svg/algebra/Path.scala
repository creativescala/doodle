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
import doodle.core.PathElement
import doodle.core.{Transform => Tx}

import scala.collection.mutable

trait PathModule { root: Base with SvgModule =>
  trait Path extends GenericPath[SvgResult] {
    self: doodle.algebra.Algebra {
      type Drawing[A] = doodle.algebra.generic.Finalized[SvgResult, A]
    } =>
    object PathApi extends PathApi {
      val b = bundle
      import b.implicits._
      import b.{svgAttrs, svgTags}

      def closedPath(
          tx: Tx,
          fill: Option[Fill],
          stroke: Option[Stroke],
          elements: List[PathElement]
      ): SvgResult[Unit] = {
        val dAttr = Svg.toSvgPath(elements, Svg.Closed)
        val set = mutable.Set.empty[root.Tag]
        val style = Svg.toStyle(stroke, fill, set)
        val elt = svgTags.path(
          svgAttrs.transform := Svg.toSvgTransform(tx),
          svgAttrs.style := style,
          svgAttrs.d := dAttr
        )

        (elt, set, ())
      }

      def openPath(
          tx: Tx,
          fill: Option[Fill],
          stroke: Option[Stroke],
          elements: List[PathElement]
      ): SvgResult[Unit] = {
        val dAttr = Svg.toSvgPath(elements, Svg.Open)
        val set = mutable.Set.empty[root.Tag]
        val style = Svg.toStyle(stroke, fill, set)
        val elt = svgTags.path(
          svgAttrs.transform := Svg.toSvgTransform(tx),
          svgAttrs.style := style,
          svgAttrs.d := dAttr
        )

        (elt, set, ())
      }
    }
  }
}
