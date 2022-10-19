/*
 * Copyright 2015-2020 Noel Welsh
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
import doodle.algebra.Layout
import doodle.algebra.Picture
import doodle.core.Angle
import doodle.core.Landmark
import doodle.core.Point
import doodle.core.Vec

trait LayoutSyntax {
  implicit class LayoutPictureOps[Alg <: Layout, A](
      picture: Picture[Alg, A]
  ) {
    def on(
        that: Picture[Alg, A]
    )(implicit s: Semigroup[A]): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.on(picture(algebra), that(algebra))
      }

    def beside(
        that: Picture[Alg, A]
    )(implicit s: Semigroup[A]): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.beside(picture(algebra), that(algebra))
      }

    def above(
        that: Picture[Alg, A]
    )(implicit s: Semigroup[A]): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.above(picture(algebra), that(algebra))
      }

    def under(
        that: Picture[Alg, A]
    )(implicit s: Semigroup[A]): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.under(picture(algebra), that(algebra))
      }

    def below(
        that: Picture[Alg, A]
    )(implicit s: Semigroup[A]): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.below(picture(algebra), that(algebra))
      }

    def at(landmark: Landmark): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.at(picture(algebra), landmark)
      }

    def at(x: Double, y: Double): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.at(picture(algebra), x, y)
      }

    def at(r: Double, a: Angle): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.at(picture(algebra), r, a)
      }

    def at(offset: Vec): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.at(picture(algebra), offset)
      }

    def at(offset: Point): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.at(picture(algebra), offset)
      }

    def originAt(landmark: Landmark): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.originAt(picture(algebra), landmark)
      }

    def originAt(x: Double, y: Double): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.originAt(picture(algebra), x, y)
      }

    def originAt(r: Double, a: Angle): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.originAt(picture(algebra), r, a)
      }

    def originAt(offset: Vec): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.originAt(picture(algebra), offset)
      }

    def originAt(offset: Point): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.originAt(picture(algebra), offset)
      }

    def margin(
        top: Double,
        right: Double,
        bottom: Double,
        left: Double
    ): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.margin(picture(algebra), top, right, bottom, left)
      }

    def margin(width: Double, height: Double): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.margin(picture(algebra), width, height)
      }

    def margin(width: Double): Picture[Alg, A] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[A] =
          algebra.margin(picture(algebra), width)
      }
  }
}
