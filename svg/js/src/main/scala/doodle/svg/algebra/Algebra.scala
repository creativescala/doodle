package doodle
package svg
package algebra

import doodle.language.Basic
import doodle.svg.effect.SvgModule

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
  val algebraInstance = new BaseAlgebra with MouseOver {}
}
