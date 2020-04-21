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
    with ReifiedText
    with GenericDebug[Reification]
    with GenericLayout[Reification]
    with GenericSize[Reification]
    with GenericStyle[Reification]
    with GenericTransform[Reification]
    with GivenApply[Reification]
    with GivenFunctor[Reification] {
}
object TestAlgebra {
  import doodle.algebra._

  type Algebra[F[_]] =
    Layout[F] with Size[F] with Path[F] with Shape[F] with Debug[F] with Style[F] with Text[F]
  type Drawing[A] = Finalized[Reification, A]
}
