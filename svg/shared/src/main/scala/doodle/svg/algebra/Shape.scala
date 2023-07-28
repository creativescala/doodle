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
import doodle.core.Point
import doodle.core.{Transform => Tx}

import scala.collection.mutable

trait ShapeModule { root: Base with SvgModule =>
  trait Shape extends GenericShape[SvgResult] {
    self: doodle.algebra.Algebra {
      type Drawing[A] = doodle.algebra.generic.Finalized[SvgResult, A]
    } =>
    object ShapeApi extends ShapeApi {
      val b = bundle
      import b.implicits._
      import b.{svgAttrs, svgTags}

      def rectangle(
          tx: Tx,
          fill: Option[Fill],
          stroke: Option[Stroke],
          width: Double,
          height: Double
      ): SvgResult[Unit] = {
        val x = -(width / 2.0)
        val y = -(height / 2.0)
        val set = mutable.Set.empty[root.Tag]
        val style = Svg.toStyle(stroke, fill, set)
        val elt = svgTags.rect(
          svgAttrs.transform := Svg.toSvgTransform(tx),
          svgAttrs.style := style,
          svgAttrs.x := x,
          svgAttrs.y := y,
          svgAttrs.width := width,
          svgAttrs.height := height
        )

        (elt, set, ())
      }

      def triangle(
          tx: Tx,
          fill: Option[Fill],
          stroke: Option[Stroke],
          width: Double,
          height: Double
      ): SvgResult[Unit] = {
        val w = width / 2.0
        val h = height / 2.0
        val points = Array(Point(-w, -h), Point(0, h), Point(w, -h))
        val dAttr = Svg.toSvgPath(points, Svg.Closed)
        val set = mutable.Set.empty[root.Tag]
        val style = Svg.toStyle(stroke, fill, set)
        val elt = svgTags.path(
          svgAttrs.transform := Svg.toSvgTransform(tx),
          svgAttrs.style := style,
          svgAttrs.d := dAttr
        )

        (elt, set, ())
      }

      def circle(
          tx: Tx,
          fill: Option[Fill],
          stroke: Option[Stroke],
          diameter: Double
      ): SvgResult[Unit] = {
        val set = mutable.Set.empty[root.Tag]
        val style = Svg.toStyle(stroke, fill, set)
        val elt = svgTags.circle(
          svgAttrs.transform := Svg.toSvgTransform(tx),
          svgAttrs.style := style,
          svgAttrs.r := (diameter / 2.0)
        )

        (elt, set, ())
      }

      def unit: SvgResult[Unit] = {
        (svgTags.g(), mutable.Set.empty, ())
      }
    }
  }
}
