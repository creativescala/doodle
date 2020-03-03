package doodle
package svg
package algebra

import cats._
import doodle.core.BoundingBox
import doodle.core.font.Font
import doodle.language.Basic
import doodle.svg.effect.Canvas

trait JsAlgebraModule
    extends AlgebraModule
    with MouseOverModule
    with PathModule
    with ShapeModule
    with SvgModule
    with TextModule
    with JsBase {
  type Algebra[F[_]] = doodle.algebra.Algebra[F]
    with doodle.interact.algebra.MouseOver[F]
    with doodle.algebra.Text[F]
    with Basic[F]

  final class JsAlgebra(
      val canvas: Canvas,
      val applyF: Apply[SvgResult],
      val functorF: Functor[SvgResult]
  ) extends BaseAlgebra
      with HasTextBoundingBox
      with MouseOver
      with Text {
    def textBoundingBox(text: String, font: Font): BoundingBox =
      canvas.textBoundingBox(text, font)
  }
}
