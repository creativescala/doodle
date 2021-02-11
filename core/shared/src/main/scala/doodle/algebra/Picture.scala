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
package algebra

import cats._
import cats.implicits._

/**
  * Represents a picture, which is a function from a tagless final algebra to
  * some type F that represents drawing a picture with result A. Has a monad
  * instance if F does.
  */
trait Picture[-Alg[x[_]] <: Algebra[x], F[_], A] {
  def apply(implicit algebra: Alg[F]): F[A]
}
object Picture {
  def apply[Alg[x[_]] <: Algebra[x], F[_], A](
      f: Alg[F] => F[A]
  ): Picture[Alg, F, A] = {
    new Picture[Alg, F, A] {
      def apply(implicit algebra: Alg[F]): F[A] =
        f(algebra)
    }
  }

  /**
    * Picture[Alg,F,?] has a Monoid instance if:
    *
    * - the algebra has `Layout` and `Shape`;
    * - the effect type has a `Functor`; and
    * - and the result type has a `Monoid`.
    *
    * In this case the combine is `on`, with identity `empty`.
    */
  implicit def pictureMonoidInstance[Alg[x[_]] <: Layout[x] with Shape[x],
                                     F[_],
                                     A](
      implicit f: Functor[F],
      m: Monoid[A]
  ): Monoid[Picture[Alg, F, A]] =
    new Monoid[Picture[Alg, F, A]] {
      val empty: Picture[Alg, F, A] =
        Picture(alg => alg.empty.map(_ => m.empty))

      def combine(x: Picture[Alg, F, A],
                  y: Picture[Alg, F, A]): Picture[Alg, F, A] =
        Picture(alg => alg.on(x(alg), y(alg)))
    }

  /**
    * Picture[Alg,F,?] has a Monad instance if F does
    */
  implicit def pictureMonadInstance[Alg[x[_]] <: Algebra[x], F[_]](
      implicit m: Monad[F]
  ): Monad[Picture[Alg, F, ?]] =
    new Monad[Picture[Alg, F, ?]] {
      def flatMap[A, B](
          fa: Picture[Alg, F, A]
      )(f: A => Picture[Alg, F, B]): Picture[Alg, F, B] =
        Picture(alg => fa(alg).flatMap(a => f(a)(alg)))

      def pure[A](x: A): Picture[Alg, F, A] =
        Picture(_ => x.pure[F])

      def tailRecM[A, B](
          a: A
      )(f: A => Picture[Alg, F, Either[A, B]]): Picture[Alg, F, B] =
        f(a).flatMap(
          either =>
            either match {
              case Left(a)  => tailRecM(a)(f)
              case Right(b) => pure(b)
          }
        )
    }
}
