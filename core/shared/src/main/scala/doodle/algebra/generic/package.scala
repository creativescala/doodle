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
import cats.data._
import cats.implicits._
import doodle.core.{BoundingBox, Transform => Tx}

package object generic {
  type ContextTransform = DrawingContext => DrawingContext

  /** A [[Finalized]] represents an effect that, when run, produces all the
    * information needed to layout an image (it "finalizes" all the information
    * needed for this process) and can eventually produce a value of type `A`
    * (once it is rendered). Algorithmically this means:
    *
    * - for each shape work out its [[DrawingContext]] from which we can work
    *   out a [[doodle.core.BoundingBox]].
    *
    * - apply transforms to bounding boxes at the point they are defined so each
    *   transformed subtree is laid out in its local coordinate system.
    *
    * The List of ContextTransform's are supplied in the order they should be
    * applied: the innermost transform is at the head of the list.
    */
  type Finalized[F[_], A] =
    State[List[ContextTransform], (BoundingBox, Renderable[F, A])]
  object Finalized {
    def apply[F[_], A](
        f: List[ContextTransform] => (List[ContextTransform],
                                      (BoundingBox, Renderable[F, A])))
      : Finalized[F, A] =
      State[List[ContextTransform], (BoundingBox, Renderable[F, A])] { f }

    /** Create a leaf [[Finalized]]. It will be passed a [[DrawingContext]] with all
      * transforms applied in the correct order.
      */
    def leaf[F[_], A](
        f: DrawingContext => (BoundingBox, Renderable[F, A])): Finalized[F, A] =
      State.inspect { ctxTxs =>
        val dc = ctxTxs.foldLeft(DrawingContext.default) { (dc, f) =>
          f(dc)
        }
        f(dc)
      }

    def contextTransform[F[_], A](f: DrawingContext => DrawingContext)(
        child: Finalized[F, A]): Finalized[F, A] = {
      for {
        _ <- State.modify { (ctxTxs: List[ContextTransform]) =>
          f :: ctxTxs
        }
        a <- child
      } yield a
    }

    def transform[F[_], A](transform: Tx)(
        child: Finalized[F, A]): Finalized[F, A] =
      child.map {
        case (bb, rdr) =>
          (bb.transform(transform), rdr.contramap(tx => transform.andThen(tx)))
      }
  }
  implicit class FinalizedOps[F[_],A](finalized: Finalized[F,A]) {
    def boundingBox: BoundingBox = {
      val (bb, _) = finalized.runA(List.empty).value
      bb
    }
  }

  /** A [[Renderable]] represents some effect producing a value of type A and also
    * creating a concrete implementation specific drawing.
    *
    * Invoking a [[Renderable]] does any layout (usually using bounding box
    * information calculated in [[Finalized]]) and as such requires a
    * [[doodle.core.Transform]]. Transforms should be applied outermost last. So
    * any transformation in a [[Renderable]] should be applied before the
    * trasform it receives from its surrounding context. */
  type Renderable[F[_], A] = State[Tx, F[A]]
  object Renderable {
    def parallel[F[_]: Apply, A: Semigroup](txLeft: Tx, txRight: Tx)(
        left: Renderable[F, A])(right: Renderable[F, A]): Renderable[F, A] =
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
        child: Renderable[F, A]): Renderable[F, A] =
      child.contramap(tx => transform.andThen(tx))

    def apply[F[_], A](f: Tx => Eval[F[A]]): Renderable[F, A] =
      IndexedStateT.inspectF(f)

    implicit def renderableSemigroup[F[_]: Apply, A: Semigroup]
      : Semigroup[Renderable[F, A]] =
      new Semigroup[Renderable[F, A]] {
        def combine(x: Renderable[F, A],
                    y: Renderable[F, A]): Renderable[F, A] =
          Renderable(tx =>
            (x.runA(tx), y.runA(tx)).mapN { (fx, fy) =>
              (fx, fy).mapN((a, b) => a |+| b)
          })
      }
  }
}
