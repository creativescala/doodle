/*
 * Copyright 2015 noelwelsh
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
import doodle.algebra.{Image,Layout}
import doodle.core.{Point,Vec}

trait LayoutSyntax {
  implicit class LayoutOps[F[_],A](image: F[A]) {
    def on(that: F[A])(implicit l: Layout[F], s: Semigroup[A]): F[A] =
      l.on(image, that)

    def beside(that: F[A])(implicit l: Layout[F], s: Semigroup[A]): F[A] =
      l.beside(image, that)

    def above(that: F[A])(implicit l: Layout[F], s: Semigroup[A]): F[A] =
      l.above(image, that)

    def under(that: F[A])(implicit l: Layout[F], s: Semigroup[A]): F[A] =
      l.under(image, that)

    def below(that: F[A])(implicit l: Layout[F], s: Semigroup[A]): F[A] =
      l.below(image, that)

    def at(x: Double, y: Double)(implicit l: Layout[F]): F[A] =
      l.at(image, x, y)

    def at(offset: Vec)(implicit l: Layout[F]): F[A] =
      l.at(image, offset)

    def at(offset: Point)(implicit l: Layout[F]): F[A] =
      l.at(image, offset)
  }

  implicit class LayoutImageOps[Algebra <: Layout[F],F[_],A](image: Image[Algebra,F,A]) {
    def on(that: Image[Algebra,F,A])(implicit s: Semigroup[A]): Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).on(that(algebra))
      }

    def beside(that: Image[Algebra,F,A])(implicit s: Semigroup[A]): Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).beside(that(algebra))
      }

    def above(that: Image[Algebra,F,A])(implicit s: Semigroup[A]): Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).above(that(algebra))
      }

    def under(that: Image[Algebra,F,A])(implicit s: Semigroup[A]): Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).under(that(algebra))
      }

    def below(that: Image[Algebra,F,A])(implicit s: Semigroup[A]): Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).below(that(algebra))
      }
  }
}
