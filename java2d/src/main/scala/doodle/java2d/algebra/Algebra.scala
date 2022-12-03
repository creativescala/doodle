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
