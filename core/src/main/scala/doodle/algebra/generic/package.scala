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

import cats.Id
import cats.data.{Kleisli,Reader}
import cats.effect.IO
import doodle.core.{Point,Transform => Tx}

package object generic {
  type ContextTransform = DrawingContext => DrawingContext

  /** Given a List of [[ContextTransform]] return a [[BoundingBox]] around a drawing and a
    * [[Contextualized]] rendering of that drawing.
    *
    * The List of ContextTransform's are supplied in the order they should be
    * applied: the innermost transform is at the head of the list.
    */
  type Finalized[G,A] =
    Reader[List[ContextTransform],(BoundingBox, Contextualized[G,A])]
  object Finalized {
    def apply[G,A](f: List[ContextTransform] => (BoundingBox, Contextualized[G,A])): Finalized[G,A] =
      Kleisli[Id, List[ContextTransform], (BoundingBox, Contextualized[G,A])](f)

    /** Create a leaf [[Finalized]]. It will be passed a [[DrawingContext]] with all
      * transforms applied in the correct order.
      */
    def leaf[G,A](f: DrawingContext => (BoundingBox, Contextualized[G,A])): Finalized[G,A] =
      Kleisli[Id, List[ContextTransform], (BoundingBox, Contextualized[G,A])]{ ctxTxs =>
        val dc = ctxTxs.foldLeft(DrawingContext.default){ (dc, f) => f(dc) }
        f(dc)
      }

    def contextTransform[G,A](f: DrawingContext => DrawingContext)(child: Finalized[G,A]): Finalized[G,A] = {
      apply{ ctxTxs: List[ContextTransform] =>
        child(f :: ctxTxs)
      }
    }
  }

  /** Construct a [[Renderable]] given a graphics context of type `G` and a [[doodle.core.Transform]] from logical to screen space. */
  type Contextualized[G,A] = Reader[(G, Tx),Renderable[A]]
  object Contextualized {
    def apply[G,A](f: (G, Tx) => Renderable[A]): Contextualized[G,A] =
      Kleisli{ case (gc, tx) => f(gc, tx) }

    def apply[G,A](f: ((G, Tx)) => Renderable[A]): Contextualized[G,A] =
      Kleisli[Id, (G, Tx), Renderable[A]]{ f }
  }

  /** Given an origin point returns an effect that renders a drawing and returns
    * result of type A. */
  type Renderable[A] = Reader[Point,IO[A]]
  object Renderable {
    def apply[A](f: Point => IO[A]): Renderable[A] =
      Kleisli[Id, Point, IO[A]](f)
  }
}
