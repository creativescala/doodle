package doodle
package algebra
package generic

import cats.instances.unit._
import org.scalacheck.Prop._
import org.scalacheck._

object GenericAlgebraspec extends Properties("Generic algebra properties") {
  implicit val algebra: TestAlgebra = TestAlgebra()

  property("Beside doubles the width") = forAll(Generators.finalized) { f =>
    val (bbDouble, _) = algebra.beside(f, f).runA(List.empty).value
    val (bb, _) = f.runA(List.empty).value

    bbDouble.width ?= (bb.width * 2)
    bbDouble.height ?= bb.height
  }

  property("Above doubles the height") = forAll(Generators.finalized) { f =>
    val (bbDouble, _) = algebra.above(f, f).runA(List.empty).value
    val (bb, _) = f.runA(List.empty).value

    bbDouble.height ?= (bb.height * 2)
    bbDouble.width ?= bb.width
  }

  property("On doesn't change the bounding box") =
    forAll(Generators.finalized) { f =>
      val (bbDouble, _) = algebra.on(f, f).runA(List.empty).value
      val (bb, _) = f.runA(List.empty).value

      bbDouble.height ?= bb.height
      bbDouble.width ?= bb.width
    }
}
