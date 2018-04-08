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
import doodle.core.Point

package object generic {
  type Transform = Point => Point

  /** Given a [[DrawingContext]] return a [[BoundingBox]] around a drawing and a [[Contextualized]] rendering of that drawing. */
  type Finalized[G,A] = Reader[DrawingContext,(BoundingBox, Contextualized[G,A])]
  object Finalized {
    def apply[G,A](f: DrawingContext => (BoundingBox, Contextualized[G,A])): Finalized[G,A] =
      Kleisli[Id, DrawingContext, (BoundingBox, Contextualized[G,A])](f)

    def contextTransform[G,A](f: DrawingContext => DrawingContext)(child: Finalized[G,A]): Finalized[G,A] = {
      apply{ dc =>
        val transformed = f(dc)
        child(transformed)
      }
    }
  }

  /** Construct a [[Renderable]] given a graphics context of type `G` and a [[Transform]] from logical to screen space. */
  type Contextualized[G,A] = Reader[(G, Transform),Renderable[A]]
  object Contextualized {
    def apply[G,A](f: (G, Transform) => Renderable[A]): Contextualized[G,A] =
      Kleisli{ case (gc, tx) => f(gc, tx) }

    def apply[G,A](f: ((G, Transform)) => Renderable[A]): Contextualized[G,A] =
      Kleisli[Id, (G, Transform), Renderable[A]]{ f }
  }

  /** Given an origin point returns an effect that renders a drawing and returns
    * result of type A. */
  type Renderable[A] = Reader[Point,IO[A]]
  object Renderable {
    def apply[A](f: Point => IO[A]): Renderable[A] =
      Kleisli[Id, Point, IO[A]](f)
  }
}
