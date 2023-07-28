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
import doodle.java2d.algebra.Java2D

import java.awt.geom.Rectangle2D
import javax.swing.JPanel

trait JvmAlgebraModule
    extends AlgebraModule
    with PathModule
    with ShapeModule
    with SvgModule
    with TextModule
    with JvmBase {
  type Algebra = JvmAlgebra

  final class JvmAlgebra()
      extends JPanel(false)
      with BaseAlgebra
      with Text
      with HasTextBoundingBox[Rectangle2D] {

    def textBoundingBox(
        text: String,
        font: Font
    ): (BoundingBox, Rectangle2D) = {
      val metrics = this.getFontMetrics(Java2D.toAwtFont(font))
      val bounds = metrics.getStringBounds(text, this.getGraphics())
      val bb = BoundingBox.centered(bounds.getWidth(), bounds.getHeight())
      (bb, bounds)
    }

    implicit val applyDrawing: cats.Apply[JvmAlgebraModule.this.SvgResult] =
      Svg.svgResultApplicative

    implicit val functorDrawing: cats.Functor[JvmAlgebraModule.this.SvgResult] =
      Svg.svgResultApplicative

    implicit val drawingInstance: Applicative[Drawing] =
      new Applicative[Drawing] {
        def pure[A](x: A): JvmAlgebra.this.Drawing[A] =
          Finalized.leaf(_ =>
            (
              BoundingBox.empty,
              Renderable(_ => Eval.now(Svg.svgResultApplicative.pure(x)))
            )
          )

        def ap[A, B](ff: JvmAlgebra.this.Drawing[A => B])(
            fa: JvmAlgebra.this.Drawing[A]
        ): JvmAlgebra.this.Drawing[B] = ???
      }
  }

  val algebraInstance = new JvmAlgebra()
}
