package doodle
package algebra
package generic

import cats.instances.unit._
import doodle.syntax._
import org.scalacheck._
import org.scalacheck.Prop._

object GenericAlgebraspec extends Properties("Generic algebra properties") {
  implicit val algebra = TestAlgebra()

  property("Beside doubles the width") =
    forAll(Generators.finalized){ f =>
      val (bbDouble, _) = f.beside(f).runA(List.empty).value
      val (bb, _) = f.runA(List.empty).value

      bbDouble.width ?= (bb.width * 2)
      bbDouble.height ?= bb.height
    }

  property("Above doubles the height") =
    forAll(Generators.finalized){ f =>
      val (bbDouble, _) = f.above(f).runA(List.empty).value
      val (bb, _) = f.runA(List.empty).value

      bbDouble.height ?= (bb.height * 2)
      bbDouble.width ?= bb.width
    }

  property("On doesn't change the bounding box") =
    forAll(Generators.finalized){ f =>
      val (bbDouble, _) = f.on(f).runA(List.empty).value
      val (bb, _) = f.runA(List.empty).value

      bbDouble.height ?= bb.height
      bbDouble.width ?= bb.width
    }
}
