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
package syntax

import cats.Semigroup
import doodle.algebra.Algebra
import doodle.algebra.Layout
import doodle.algebra.Picture
import doodle.core.Angle
import doodle.core.Landmark
import doodle.core.Point
import doodle.core.Vec

trait LayoutSyntax {
  implicit class LayoutPictureOps[Alg <: Algebra, A](
      picture: Picture[Alg, A]
  ) {
    def on[Alg2 <: Algebra](
        that: Picture[Alg2, A]
    )(implicit s: Semigroup[A]): Picture[Alg with Alg2 with Layout, A] =
      new Picture[Alg with Alg2 with Layout, A] {
        def apply(implicit
            algebra: Alg with Alg2 with Layout
        ): algebra.Drawing[A] =
          algebra.on(picture(algebra), that(algebra))
      }

    def beside[Alg2 <: Algebra](
        that: Picture[Alg2, A]
    )(implicit s: Semigroup[A]): Picture[Alg with Alg2 with Layout, A] =
      new Picture[Alg with Alg2 with Layout, A] {
        def apply(implicit
            algebra: Alg with Alg2 with Layout
        ): algebra.Drawing[A] =
          algebra.beside(picture(algebra), that(algebra))
      }

    def above[Alg2 <: Algebra](
        that: Picture[Alg2, A]
    )(implicit s: Semigroup[A]): Picture[Alg with Alg2 with Layout, A] =
      new Picture[Alg with Alg2 with Layout, A] {
        def apply(implicit
            algebra: Alg with Alg2 with Layout
        ): algebra.Drawing[A] =
          algebra.above(picture(algebra), that(algebra))
      }

    def under[Alg2 <: Algebra](
        that: Picture[Alg2, A]
    )(implicit s: Semigroup[A]): Picture[Alg with Alg2 with Layout, A] =
      new Picture[Alg with Alg2 with Layout, A] {
        def apply(implicit
            algebra: Alg with Alg2 with Layout
        ): algebra.Drawing[A] =
          algebra.under(picture(algebra), that(algebra))
      }

    def below[Alg2 <: Algebra](
        that: Picture[Alg2, A]
    )(implicit s: Semigroup[A]): Picture[Alg with Alg2 with Layout, A] =
      new Picture[Alg with Alg2 with Layout, A] {
        def apply(implicit
            algebra: Alg with Alg2 with Layout
        ): algebra.Drawing[A] =
          algebra.below(picture(algebra), that(algebra))
      }

    def at(landmark: Landmark): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.at(picture(algebra), landmark)
      }

    def at(x: Double, y: Double): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.at(picture(algebra), x, y)
      }

    def at(r: Double, a: Angle): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.at(picture(algebra), r, a)
      }

    def at(offset: Vec): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.at(picture(algebra), offset)
      }

    def at(offset: Point): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.at(picture(algebra), offset)
      }

    def originAt(landmark: Landmark): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.originAt(picture(algebra), landmark)
      }

    def originAt(x: Double, y: Double): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.originAt(picture(algebra), x, y)
      }

    def originAt(r: Double, a: Angle): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.originAt(picture(algebra), r, a)
      }

    def originAt(offset: Vec): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.originAt(picture(algebra), offset)
      }

    def originAt(offset: Point): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.originAt(picture(algebra), offset)
      }

    def margin(
        top: Double,
        right: Double,
        bottom: Double,
        left: Double
    ): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.margin(picture(algebra), top, right, bottom, left)
      }

    def margin(width: Double, height: Double): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.margin(picture(algebra), width, height)
      }

    def margin(width: Double): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.margin(picture(algebra), width)
      }

    def size(width: Double, height: Double): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.size(picture(algebra), width, height)
      }

    def size(extent: Double): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.size(picture(algebra), extent)
      }
    // Landmark-based margin methods
    def margin(
        top: Landmark,
        right: Landmark,
        bottom: Landmark,
        left: Landmark
    ): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.margin(picture(algebra), top, right, bottom, left)
      }

    def margin(
        horizontal: Landmark,
        vertical: Landmark
    ): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.margin(picture(algebra), horizontal, vertical)
      }

    def margin(all: Landmark): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.margin(picture(algebra), all)
      }

    // Landmark-based size methods
    def size(width: Landmark, height: Landmark): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.size(picture(algebra), width, height)
      }

    def size(extent: Landmark): Picture[Alg with Layout, A] =
      new Picture[Alg with Layout, A] {
        def apply(implicit algebra: Alg with Layout): algebra.Drawing[A] =
          algebra.size(picture(algebra), extent)
      }
  }
}
