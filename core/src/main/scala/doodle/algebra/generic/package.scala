/*
 * Copyright 2015 noelwelsh
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

  /** Gather the information needed to layout a drawing. This means:
    *
    * - for each shape work out its [[DrawingContext]] from which we can work
    *   out a [[BoundingBox]].
    *
    * - apply transforms at the point they are defined so each transformed
    *   subtree is laid out in its local coordinate system.
    *
    * The List of ContextTransform's are supplied in the order they should be
    * applied: the innermost transform is at the head of the list.
    */
  type Finalized[G, A] =
    Reader[List[ContextTransform], (BoundingBox, Renderable[A])]

  object Finalized {
    def apply[G, A](f: List[ContextTransform] => (BoundingBox, Renderable[A]))
      : Finalized[G, A] =
      Kleisli[Id, List[ContextTransform], (BoundingBox, Renderable[A])] { f }

    /** Create a leaf [[Finalized]]. It will be passed a [[DrawingContext]] with all
      * transforms applied in the correct order.
      */
    def leaf[G, A](
        f: DrawingContext => (BoundingBox, Renderable[A])): Finalized[G, A] =
      Kleisli[Id, List[ContextTransform], (BoundingBox, Renderable[A])] {
        ctxTxs =>
          val dc = ctxTxs.foldLeft(DrawingContext.default) { (dc, f) =>
            f(dc)
          }
          f(dc)
      }

    def contextTransform[G, A](f: DrawingContext => DrawingContext)(
        child: Finalized[G, A]): Finalized[G, A] = {
      apply { ctxTxs =>
        child(f :: ctxTxs)
      }
    }

    def transform[G, A](tx: Tx)(child: Finalized[G, A]): Finalized[G, A] =
      apply { ctxTxs =>
        val txCtxTx = (dc: DrawingContext) => dc.addTransform(tx)
        val (bb, ctxized) = child(txCtxTx :: ctxTxs)

        (bb.transform(tx), ctxized)
      }
  }

  /** Construct a [[Renderable]] given a [[doodle.core.Transform]] from logical to
    * screen space. */
  // type Contextualized[A] = Reader[Tx,Renderable[A]]
  // object Contextualized {
  //   def apply[A](f: (Tx) => Renderable[A]): Contextualized[G,A] =
  //     Kleisli{ f }

  //   def transform[A](tx: Tx)(child: Contextualized[G,A]): Contextualized[G,A] =
  //     apply{ initialTx => child((g, initialTx.andThen(tx))) }
  // }

  /** Given a transform from logical to screen, writes a list of reified drawing
    * commands and evalutes an effect of type `A`. */
  type Renderable[A] = ReaderWriterState[Tx, List[Reified], Unit, A]
  object Renderable {
    def apply(f: Tx => List[Reified]): Renderable[Unit] =
      ReaderWriterState.apply((tx, _) => (f(tx), (), ()))

    def make[A](f: Tx => Eval[(List[Reified], A)]): Renderable[A] =
      IndexedReaderWriterStateT.apply { (tx, _) =>
        f(tx).map { case (r, a) => (r, (), a) }
      }

    def tell(reified: Reified): Renderable[Unit] =
      ReaderWriterState.tell(List(reified))

    implicit def renderableSemigroup[A](
        implicit m: Semigroup[A]): Semigroup[Renderable[A]] =
      new Semigroup[Renderable[A]] {
        def combine(x: Renderable[A], y: Renderable[A]): Renderable[A] =
          Renderable.make { tx =>
            x.run(tx) |+| y.run(tx)
          }
      }
  }
  implicit class RenderableOps[A](renderable: Renderable[A]) {
    def run(transform: Tx): Eval[(List[Reified], A)] =
      renderable.run(transform, ()).map { case (r, _, a) => (r, a) }
  }
}
