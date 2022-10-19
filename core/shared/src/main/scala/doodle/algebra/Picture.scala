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

/** Represents a picture, which is a function from a tagless final algebra to
  * some type F that represents drawing a picture with result A. Has a monad
  * instance if F does.
  */
trait Picture[-Alg <: Algebra, A] { self =>
  def apply(implicit algebra: Alg): algebra.F[A]
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
        new Picture {
          def apply(implicit algebra: Alg): algebra.F[A] =
            algebra.empty.map(_ => m.empty)
        }

      def combine(
          x: Picture[Alg, A],
          y: Picture[Alg, A]
      ): Picture[Alg, A] =
        new Picture {
          def apply(implicit algebra: Alg): algebra.F[A] =
            algebra.on(x(algebra), y(algebra))
        }
    }
}
