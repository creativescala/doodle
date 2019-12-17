package doodle
package svg
package algebra

import cats._
import doodle.language.Basic

trait JvmAlgebraModule
    extends AlgebraModule
    with PathModule
    with ShapeModule
    with SvgModule
    with JvmBase {
  type Algebra[F[_]] = doodle.algebra.Algebra[F] with Basic[F]

  final class JvmAlgebra(
      val applyF: Apply[SvgResult],
      val functorF: Functor[SvgResult]
  ) extends BaseAlgebra
  val algebraInstance = new JvmAlgebra(Svg.svgResultApply, Svg.svgResultApply)
}
