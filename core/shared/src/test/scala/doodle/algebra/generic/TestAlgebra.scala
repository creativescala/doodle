/*
 * Copyright 2015 Noel Welsh
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
import cats.data._
import cats.implicits._
import doodle.algebra.generic.reified._
import doodle.core.BoundingBox

import java.awt.Graphics2D

final case class TestAlgebra(
    applyDrawing: Apply[Reification] = Apply.apply[Reification],
    functorDrawing: Functor[Reification] = Apply.apply[Reification]
) extends Algebra
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
  type Drawing[A] = Finalized[Reification, A]
  implicit val drawingInstance: Applicative[Drawing] =
    new Applicative[Drawing] {
      def ap[A, B](ff: Drawing[A => B])(fa: Drawing[A]): Drawing[B] = ???
      def pure[A](x: A): Drawing[A] =
        Finalized.leaf(_ =>
          (
            BoundingBox.empty,
            Renderable.apply(_ =>
              Eval.now(WriterT.liftF[Eval, List[Reified], A](Eval.now(x)))
            )
          )
        )
    }
}
object TestAlgebra {
  import doodle.algebra._

  type Algebra =
    Layout with Size with Path with Shape with Debug with Style with Text
  type Drawing[A] = Finalized[Reification, A]
}
