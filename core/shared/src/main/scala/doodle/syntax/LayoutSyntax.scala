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
  implicit class LayoutOps[F[_], A](picture: F[A]) {
    def on(that: F[A])(implicit l: Layout[F], s: Semigroup[A]): F[A] =
      l.on(picture, that)

    def beside(that: F[A])(implicit l: Layout[F], s: Semigroup[A]): F[A] =
      l.beside(picture, that)

    def above(that: F[A])(implicit l: Layout[F], s: Semigroup[A]): F[A] =
      l.above(picture, that)

    def under(that: F[A])(implicit l: Layout[F], s: Semigroup[A]): F[A] =
      l.under(picture, that)

    def below(that: F[A])(implicit l: Layout[F], s: Semigroup[A]): F[A] =
      l.below(picture, that)

    def at(x: Double, y: Double)(implicit l: Layout[F]): F[A] =
      l.at(picture, x, y)

    def at(offset: Vec)(implicit l: Layout[F]): F[A] =
      l.at(picture, offset)

    def at(offset: Point)(implicit l: Layout[F]): F[A] =
      l.at(picture, offset)
  }

  implicit class LayoutPictureOps[Alg[x[_]] <: Layout[x], F[_], A](
      picture: Picture[Alg, F, A]) {
    def on(that: Picture[Alg, F, A])(
        implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).on(that(algebra))
      }

    def beside(that: Picture[Alg, F, A])(
        implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).beside(that(algebra))
      }

    def above(that: Picture[Alg, F, A])(
        implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).above(that(algebra))
      }

    def under(that: Picture[Alg, F, A])(
        implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).under(that(algebra))
      }

    def below(that: Picture[Alg, F, A])(
        implicit s: Semigroup[A]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).below(that(algebra))
      }
  }
}
