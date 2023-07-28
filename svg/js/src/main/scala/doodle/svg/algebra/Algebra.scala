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

import cats._
import doodle.algebra.generic.Finalized
import doodle.algebra.generic.Renderable
import doodle.core.BoundingBox
import doodle.core.font.Font
import doodle.svg.effect.Canvas
import org.scalajs.dom.svg.Rect

trait JsAlgebraModule
    extends AlgebraModule
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
      with Text
      with HasTextBoundingBox[Rect] {
    def textBoundingBox(text: String, font: Font): (BoundingBox, Rect) =
      canvas.textBoundingBox(text, font)

    implicit val drawingInstance: cats.Applicative[JsAlgebra.this.Drawing] =
      new Applicative[Drawing] {
        def pure[A](x: A): JsAlgebra.this.Drawing[A] =
          Finalized.leaf(_ =>
            (
              BoundingBox.empty,
              Renderable(_ => Eval.now(Svg.svgResultApplicative.pure(x)))
            )
          )

        def ap[A, B](ff: JsAlgebra.this.Drawing[A => B])(
            fa: JsAlgebra.this.Drawing[A]
        ): JsAlgebra.this.Drawing[B] = ???
      }

    implicit val applyDrawing: cats.Apply[JsAlgebraModule.this.SvgResult] =
      Svg.svgResultApplicative

    implicit val functorDrawing: cats.Functor[JsAlgebraModule.this.SvgResult] =
      Svg.svgResultApplicative
  }
}
