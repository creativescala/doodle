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
import doodle.algebra.{Picture, Layout}
import doodle.core.{Point, Vec}

trait LayoutSyntax {
  implicit class LayoutPictureOps[Alg[x[_]] <: Layout[x], F[_], A](
      picture: Picture[Alg, F, A]) {
    def on(that: Picture[Alg, F, A])(
        implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.on(picture(algebra), that(algebra))
      }

    def beside(that: Picture[Alg, F, A])(
        implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.beside(picture(algebra), that(algebra))
      }

    def above(that: Picture[Alg, F, A])(
        implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.above(picture(algebra), that(algebra))
      }

    def under(that: Picture[Alg, F, A])(
        implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.under(picture(algebra), that(algebra))
      }

    def below(that: Picture[Alg, F, A])(
        implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.below(picture(algebra), that(algebra))
      }

    def at(x: Double, y: Double): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.at(picture(algebra), x, y)
      }

    def at(offset: Vec): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.at(picture(algebra), offset)
      }

    def at(offset: Point): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.at(picture(algebra), offset)
      }
  }
}
