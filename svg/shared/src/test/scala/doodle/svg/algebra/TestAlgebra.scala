package doodle
package svg
package algebra

trait TestAlgebra
    extends AlgebraModule
    with PathModule
    with ShapeModule
    with SvgModule
    with TestBase {
  val algebraInstance = new BaseAlgebra {}
}
object TestAlgebra extends TestAlgebra {}
