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

import cats._

/** Represents a picture, which is a function from a tagless final algebra to
  * some type F that represents drawing a picture with result A. Has a monad
  * instance if F does.
  */
trait Picture[-Alg <: Algebra, A] { self =>
  def apply(implicit algebra: Alg): algebra.Drawing[A]

  def flatMap[B, AAlg <: Alg](f: A => Picture[AAlg, B]): Picture[AAlg, B] = {
    val self = this
    new Picture[AAlg, B] {
      def apply(implicit algebra: AAlg): algebra.Drawing[B] =
        algebra.drawingInstance.flatMap(self.apply(algebra))(a =>
          f(a).apply(algebra)
        )
    }
  }

  /** Utility to change the Algebra of this Picture to a subtype. This is
    * occasionally useful when you need to give type inference a hint as to what
    * to infer.
    */
  def widen[AAlg <: Alg]: Picture[AAlg, A] =
    this
}
object Picture {

  /** Picture[Alg,A] has a Monoid instance if:
    *
    *   - the algebra has `Layout` and `Shape`; and
    *   - and the result type has a `Monoid`.
    *
    * In this case the combine is `on`, with identity `empty`.
    */
  implicit def pictureMonoidInstance[Alg <: Layout with Shape, A](implicit
      m: Monoid[A]
  ): Monoid[Picture[Alg, A]] =
    new Monoid[Picture[Alg, A]] {
      val empty: Picture[Alg, A] =
        new Picture[Alg, A] {
          def apply(implicit algebra: Alg): algebra.Drawing[A] =
            algebra.drawingInstance.as(algebra.empty, m.empty)
        }

      def combine(
          x: Picture[Alg, A],
          y: Picture[Alg, A]
      ): Picture[Alg, A] =
        new Picture[Alg, A] {
          def apply(implicit algebra: Alg): algebra.Drawing[A] =
            algebra.on(x(algebra), y(algebra))
        }
    }

  implicit def pictureMonadInstance[Alg <: Algebra]: Monad[Picture[Alg, *]] =
    new Monad[Picture[Alg, *]] {
      def pure[A](x: A): Picture[Alg, A] =
        new Picture[Alg, A] {
          def apply(implicit algebra: Alg): algebra.Drawing[A] =
            algebra.drawingInstance.pure(x)
        }

      def flatMap[A, B](
          fa: Picture[Alg, A]
      )(f: A => Picture[Alg, B]): Picture[Alg, B] =
        fa.flatMap(f)

      def tailRecM[A, B](
          a: A
      )(f: A => Picture[Alg, Either[A, B]]): Picture[Alg, B] =
        new Picture[Alg, B] {
          def apply(implicit algebra: Alg): algebra.Drawing[B] =
            algebra.drawingInstance.tailRecM(a)(a => f(a).apply(algebra))
        }

    }
}
