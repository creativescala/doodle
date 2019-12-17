package doodle
package svg
package algebra

import cats._
import doodle.language.Basic

trait JsAlgebraModule
    extends AlgebraModule
    with MouseOverModule
    with PathModule
    with ShapeModule
    with SvgModule
    with JsBase {
  type Algebra[F[_]] = doodle.algebra.Algebra[F]
    with doodle.interact.algebra.MouseOver[F]
    with Basic[F]

  final class JsAlgebra(
      val applyF: Apply[SvgResult],
      val functorF: Functor[SvgResult]
  ) extends BaseAlgebra
      with MouseOver
  val algebraInstance = new JsAlgebra(Svg.svgResultApply, Svg.svgResultApply)
}
