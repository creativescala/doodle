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
import cats.data._
import cats.implicits._
import doodle.core.{Transform => Tx}

package object generic {
  type ContextTransform = DrawingContext => DrawingContext

  /** A [[Finalized]] represents an effect that, when run, produces all the
    * information needed to layout an image (it "finalizes" all the information
    * needed for this process) and can eventually produce a value of type `A`
    * (once it is rendered). Algorithmically this means:
    *
    * - for each shape work out its [[DrawingContext]] from which we can work
    *   out a [[BoundingBox]].
    *
    * - apply transforms to bounding boxes at the point they are defined so each
    *   transformed subtree is laid out in its local coordinate system.
    *
    * The List of ContextTransform's are supplied in the order they should be
    * applied: the innermost transform is at the head of the list.
    */
  type Finalized[A] =
    State[List[ContextTransform], (BoundingBox, Renderable[A])]

  object Finalized {
    def apply[A](
        f: List[ContextTransform] => (List[ContextTransform],
                                      (BoundingBox, Renderable[A])))
      : Finalized[A] =
      State[List[ContextTransform], (BoundingBox, Renderable[A])] { f }

    /** Create a leaf [[Finalized]]. It will be passed a [[DrawingContext]] with all
      * transforms applied in the correct order.
      */
    def leaf[A](
        f: DrawingContext => (BoundingBox, Renderable[A])): Finalized[A] =
      State.inspect { ctxTxs =>
        val dc = ctxTxs.foldLeft(DrawingContext.default) { (dc, f) =>
          f(dc)
        }
        f(dc)
      }

    def contextTransform[A](f: DrawingContext => DrawingContext)(
        child: Finalized[A]): Finalized[A] = {
      for {
        _ <- State.modify { (ctxTxs: List[ContextTransform]) =>
          f :: ctxTxs
        }
        a <- child
      } yield a
    }

    def transform[A](transform: Tx)(child: Finalized[A]): Finalized[A] =
      child.map {
        case (bb, rdr) =>
          (bb.transform(transform), rdr.contramap(tx => transform.andThen(tx)))
      }
  }

  /** A [[Renderable]] represents some effect producing a value of type A and also
    * producing a [[Reified]] representation of a drawing.
    *
    * Invoking a [[Renderable]] does any layout (usually using bounding box
    * information calculated in [[Finalized]]) and as such requires a
    * [[doodle.core.Transform]]. Transforms should be applied outermost last. So
    * any transformation in a [[Renderable]] should be applied before the
    * trasform it receives from its surrounding context. */
  type Renderable[A] = ReaderWriterState[Unit, List[Reified], Tx, A]
  object Renderable {
    def parallel[A: Semigroup](txLeft: Tx, txRight: Tx)(left: Renderable[A])(
        right: Renderable[A]): Renderable[A] =
      left.contramap(tx => txLeft.andThen(tx)) |+| right.contramap(tx =>
        txRight.andThen(tx))

    def unit(reified: List[Reified]): Renderable[Unit] =
      apply { _ =>
        Eval.now((reified, ()))
      }

    def transform[A](transform: Tx)(child: Renderable[A]): Renderable[A] =
      child.contramap(tx => transform.andThen(tx))

    def apply[A](f: Tx => Eval[(List[Reified], A)]): Renderable[A] =
      IndexedReaderWriterStateT.apply { (_, tx) =>
        f(tx).map { case (r, a) => (r, tx, a) }
      }

    implicit def renderableSemigroup[A](
        implicit m: Semigroup[A]): Semigroup[Renderable[A]] =
      new Semigroup[Renderable[A]] {
        def combine(x: Renderable[A], y: Renderable[A]): Renderable[A] =
          Renderable { tx =>
            (x.run((), tx), y.run((), tx)).mapN { (x, y) =>
              val (reifiedX, _, aX) = x
              val (reifiedY, _, aY) = y

              (reifiedX |+| reifiedY, aX |+| aY)
            }
          }
      }
  }
}
