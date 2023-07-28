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

import cats.*
import cats.data.*
import cats.syntax.all.*
import doodle.core.BoundingBox
import doodle.core.{Transform => Tx}

package object generic {
  type ContextTransform = DrawingContext => DrawingContext
  type Transforms = List[ContextTransform]

  /** A [[Renderable]] represents some effect producing a value of type A and
    * also creating a concrete implementation specific drawing.
    *
    * Invoking a [[Renderable]] does any layout (usually using bounding box
    * information calculated in [[Finalized]]) and as such requires a
    * [[doodle.core.Transform]]. Transforms should be applied outermost last. So
    * any transformation in a [[Renderable]] should be applied before the
    * transform it receives from its surrounding context.
    */
  type Renderable[F[_], A] = State[Tx, F[A]]
  object Renderable {
    def parallel[F[_]: Apply, A: Semigroup](txLeft: Tx, txRight: Tx)(
        left: Renderable[F, A]
    )(right: Renderable[F, A]): Renderable[F, A] =
      // Can't use the Applicative instance here as that will sequentially
      // compose the left and right, which will result in the transforms being
      // sequentially composed, which is wrong. Couldn't get `parMapN` to
      // compile so wrote it by hand.
      IndexedStateT.inspectF { tx =>
        val l = left.runA(txLeft.andThen(tx))
        val r = right.runA(txRight.andThen(tx))

        (l, r).mapN((fx, fy) => (fx, fy).mapN((a, b) => a |+| b))
      }

    def unit[F[_]](fUnit: F[Unit]): Renderable[F, Unit] =
      State.pure(fUnit)

    def transform[F[_], A](transform: Tx)(
        child: Renderable[F, A]
    ): Renderable[F, A] =
      child.contramap(tx => transform.andThen(tx))

    def apply[F[_], A](f: Tx => Eval[F[A]]): Renderable[F, A] =
      IndexedStateT.inspectF(f)

    implicit def renderableSemigroup[F[_]: Apply, A: Semigroup]
        : Semigroup[Renderable[F, A]] =
      new Semigroup[Renderable[F, A]] {
        def combine(
            x: Renderable[F, A],
            y: Renderable[F, A]
        ): Renderable[F, A] =
          Renderable(tx =>
            (x.runA(tx), y.runA(tx)).mapN { (fx, fy) =>
              (fx, fy).mapN((a, b) => a |+| b)
            }
          )
      }
  }
}
