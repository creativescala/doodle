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
package java2d
package algebra

import cats._
import cats.data.WriterT
import cats.implicits._
import doodle.algebra.generic._
import doodle.core.BoundingBox
import doodle.java2d.algebra.reified._
import doodle.language.Basic

import java.awt.Graphics2D

final case class Algebra(
    gc: Graphics2D,
    applyDrawing: Apply[Reification] = Apply.apply[Reification],
    functorDrawing: Functor[Reification] = Apply.apply[Reification]
) extends Basic
    with Java2dFromBufferedImage
    with Java2dFromBase64
    with ReifiedBitmap
    with ReifiedClip
    with ReifiedPath
    with ReifiedShape
    with ReifiedText
    with GenericDebug[Reification]
    with GenericLayout[Reification]
    with GenericSize[Reification]
    with GenericStyle[Reification]
    with GenericTransform[Reification]
    with GivenApply[Reification]
    with GivenFunctor[Reification]
    with doodle.algebra.Algebra {
  type Drawing[A] = doodle.java2d.Drawing[A]
  implicit val drawingInstance: Monad[Drawing] =
    new Monad[Drawing] {
      def pure[A](x: A): Drawing[A] =
        Finalized.leaf(_ =>
          (
            BoundingBox.empty,
            Renderable.apply(_ =>
              Eval.now(WriterT.liftF[Eval, List[Reified], A](Eval.now(x)))
            )
          )
        )

      def flatMap[A, B](fa: Drawing[A])(f: A => Drawing[B]): Drawing[B] =
        fa.flatMap { (bb, rdr) =>
          val reified = rdr.runA(doodle.core.Transform.identity).value
          val (_, a) = reified.run.value
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
