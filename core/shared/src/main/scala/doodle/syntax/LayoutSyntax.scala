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
  implicit class LayoutPictureOps[Alg[x[_]] <: Layout[x], F[_], A](
      picture: Picture[Alg, F, A]
  ) {
    def on(
        that: Picture[Alg, F, A]
    )(implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.on(picture(algebra), that(algebra))
      }

    def beside(
        that: Picture[Alg, F, A]
    )(implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.beside(picture(algebra), that(algebra))
      }

    def above(
        that: Picture[Alg, F, A]
    )(implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.above(picture(algebra), that(algebra))
      }

    def under(
        that: Picture[Alg, F, A]
    )(implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.under(picture(algebra), that(algebra))
      }

    def below(
        that: Picture[Alg, F, A]
    )(implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.below(picture(algebra), that(algebra))
      }

    def at(landmark: Landmark): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.at(picture(algebra), landmark)
      }

    def at(x: Double, y: Double): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.at(picture(algebra), x, y)
      }

    def at(r: Double, a: Angle): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.at(picture(algebra), r, a)
      }

    def at(offset: Vec): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.at(picture(algebra), offset)
      }

    def at(offset: Point): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.at(picture(algebra), offset)
      }

    def originAt(landmark: Landmark): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.originAt(picture(algebra), landmark)
      }

    def originAt(x: Double, y: Double): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.originAt(picture(algebra), x, y)
      }

    def originAt(r: Double, a: Angle): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.originAt(picture(algebra), r, a)
      }

    def originAt(offset: Vec): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.originAt(picture(algebra), offset)
      }

    def originAt(offset: Point): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.originAt(picture(algebra), offset)
      }

    def margin(
        top: Double,
        right: Double,
        bottom: Double,
        left: Double
    ): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.margin(picture(algebra), top, right, bottom, left)
      }

    def margin(width: Double, height: Double): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.margin(picture(algebra), width, height)
      }

    def margin(width: Double): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.margin(picture(algebra), width)
      }
  }
}
