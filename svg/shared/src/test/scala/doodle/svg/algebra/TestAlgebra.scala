package doodle
package svg
package algebra

import cats._

trait TestAlgebraModule
    extends AlgebraModule
    with PathModule
    with ShapeModule
    with SvgModule
    with TestBase {

  final class TestAlgebra(
      val applyF: Apply[SvgResult],
      val functorF: Functor[SvgResult]
  ) extends BaseAlgebra
  val algebraInstance = new TestAlgebra(Svg.svgResultApply, Svg.svgResultApply)
}
object TestAlgebra extends TestAlgebraModule {}
