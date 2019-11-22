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
package algebra

import cats.Monad
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
