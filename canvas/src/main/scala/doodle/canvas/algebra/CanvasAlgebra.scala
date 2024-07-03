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

package doodle.canvas.algebra

import cats.Apply
import cats.Eval
import cats.Functor
import cats.Monad
import doodle.algebra.generic._
import doodle.core.BoundingBox
import org.scalajs.dom.CanvasRenderingContext2D

final case class CanvasAlgebra(
    ctx: CanvasRenderingContext2D,
    applyDrawing: Apply[CanvasDrawing] = Apply.apply[CanvasDrawing],
    functorDrawing: Functor[CanvasDrawing] = Apply.apply[CanvasDrawing]
) extends Path,
      Raster,
      Shape,
      GenericDebug[CanvasDrawing],
      GenericLayout[CanvasDrawing],
      GenericRaster[CanvasDrawing, CanvasRenderingContext2D],
      GenericShape[CanvasDrawing],
      GenericSize[CanvasDrawing],
      GenericStyle[CanvasDrawing],
      GenericTransform[CanvasDrawing],
      GivenApply[CanvasDrawing],
      GivenFunctor[CanvasDrawing],
      doodle.algebra.Algebra {
  type Drawing[A] = doodle.canvas.Drawing[A]

  override def empty: Finalized[CanvasDrawing, Unit] =
    Finalized.leaf(_ =>
      (BoundingBox.empty, Renderable.unit(CanvasDrawing.unit))
    )

  implicit val drawingInstance: Monad[Drawing] =
    new Monad[Drawing] {
      def pure[A](x: A): Drawing[A] =
        Finalized.leaf(_ =>
          (
            BoundingBox.empty,
            Renderable.apply(_ => Eval.now(CanvasDrawing.pure(x)))
          )
        )

      def flatMap[A, B](fa: Drawing[A])(f: A => Drawing[B]): Drawing[B] =
        fa.flatMap { (bb, rdr) =>
          val canvasDrawing = rdr.runA(doodle.core.Transform.identity).value
          val a = canvasDrawing(ctx)
          f(a)
        }

      def tailRecM[A, B](a: A)(f: A => Drawing[Either[A, B]]): Drawing[B] = {
        // TODO: This implementation is not tail recursive but I don't think we need it for what we use in Doodle
        val dAB = f(a)
        flatMap(dAB)(either =>
          either match {
            case Left(a)  => tailRecM(a)(f)
            case Right(b) => dAB.asInstanceOf[Drawing[B]]
          }
        )
      }
    }
}
