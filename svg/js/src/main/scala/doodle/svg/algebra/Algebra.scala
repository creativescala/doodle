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
package svg
package algebra

import cats.*
import doodle.algebra.generic.Finalized
import doodle.algebra.generic.Renderable
import doodle.core.BoundingBox
import doodle.core.font.Font
import doodle.svg.effect.Canvas
import org.scalajs.dom.svg.Rect

trait JsAlgebraModule
    extends AlgebraModule
    with BlendModule
    with FilterModule
    with ImageModule
    with JsTaggedModule
    with PathModule
    with ShapeModule
    with SvgModule
    with TextModule
    with JsBase {
  type Algebra = JsAlgebra

  final class JsAlgebra(
      val canvas: Canvas,
      val applyF: Apply[SvgResult],
      val functorF: Functor[SvgResult]
  ) extends BaseAlgebra
      with Blend
      with Filter
      with HasTextBoundingBox[Rect]
      with Image
      with JsTagged
      with Text {
    def textBoundingBox(text: String, font: Font): (BoundingBox, Rect) =
      canvas.textBoundingBox(text, font)

    implicit val drawingInstance: cats.Monad[JsAlgebra.this.Drawing] =
      new Monad[Drawing] {
        def pure[A](x: A): JsAlgebra.this.Drawing[A] =
          Finalized.leaf(_ =>
            (
              BoundingBox.empty,
              Renderable(_ => Eval.now(Svg.svgResultApplicative.pure(x)))
            )
          )

        def flatMap[A, B](fa: Drawing[A])(f: A => Drawing[B]): Drawing[B] =
          fa.flatMap { (bb, rdr) =>
            val (_, _, a) = rdr.runA(doodle.core.Transform.identity).value
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

    implicit val applyDrawing: cats.Apply[JsAlgebraModule.this.SvgResult] =
      Svg.svgResultApplicative

    implicit val functorDrawing: cats.Functor[JsAlgebraModule.this.SvgResult] =
      Svg.svgResultApplicative
  }
}
