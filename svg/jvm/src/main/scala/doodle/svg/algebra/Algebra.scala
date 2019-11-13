package doodle
package svg
package algebra

import doodle.language.Basic
import doodle.svg.effect.SvgModule

trait JvmAlgebraModule
    extends AlgebraModule
    with PathModule
    with ShapeModule
    with SvgModule
    with JvmBase {
  type Algebra[F[_]] = doodle.algebra.Algebra[F] with Basic[F]
  val algebraInstance = new BaseAlgebra {}
}
