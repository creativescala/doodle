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
package generic

import cats._
import cats.implicits._
import doodle.algebra.Layout
import doodle.algebra.generic.reified._

final case class TestAlgebra(
    applyF: Apply[Reification] = Apply.apply[Reification],
    functorF: Functor[Reification] = Apply.apply[Reification]
) extends Algebra[Finalized[Reification, ?]]
    with ReifiedPath
    with ReifiedShape
    with GenericDebug[Reification]
    with GenericLayout[Reification]
    with GenericSize[Reification]
    with GenericStyle[Reification]
    with GenericTransform[Reification]
    with GivenApply[Reification]
    with GivenFunctor[Reification] {
  // Layout ----------------------------------------------------------

  // val layout = ReifiedLayout.instance

  // def on[A](top: Finalized[Reification, A], bottom: Finalized[Reification, A])(
  //     implicit s: Semigroup[A]
  // ): Finalized[Reification, A] =
  //   layout.on(top, bottom)(s)

  // def beside[A](
  //     left: Finalized[Reification, A],
  //     right: Finalized[Reification, A]
  // )(implicit s: Semigroup[A]): Finalized[Reification, A] =
  //   layout.beside(left, right)(s)

  // def above[A](
  //     top: Finalized[Reification, A],
  //     bottom: Finalized[Reification, A]
  // )(implicit s: Semigroup[A]): Finalized[Reification, A] =
  //   layout.above(top, bottom)(s)

  // def at[A](
  //     img: Finalized[Reification, A],
  //     x: Double,
  //     y: Double
  // ): Finalized[Reification, A] =
  //   layout.at(img, x, y)

  // Size ------------------------------------------------------------

  // val size = ReifiedSize.instance

  // def width[A](
  //     image: Finalized[Reification, A]
  // ): Finalized[Reification, Double] =
  //   size.width(image)

  // def height[A](
  //     image: Finalized[Reification, A]
  // ): Finalized[Reification, Double] =
  //   size.height(image)

  // def size[A](
  //     image: Finalized[Reification, A]
  // ): Finalized[Reification, (Double, Double)] =
  //   size.size(image)
}
object TestAlgebra {
  import doodle.algebra._

  type Algebra[F[_]] =
    Layout[F] with Size[F] with Path[F] with Shape[F] with Debug[F] with Style[F]
  type Drawing[A] = Finalized[Reification, A]
}
