package doodle
package core

import org.scalacheck.Prop._
import org.scalacheck._

object OpenPathSpec extends Properties("OpenPath properties") {
  import Generators._

  property("added element is last in the list of elements") =
    forAll(Gen.listOf(pathElement), pathElement) { (elts, elt) =>
      OpenPath(elts).add(elt).elements ?= (elts ++ List(elt))
    }

  property("appended list comes after original elements") =
    forAll(Gen.listOf(pathElement), Gen.listOf(pathElement)) { (xs, ys) =>
      OpenPath(xs).append(ys).elements ?= (xs ++ ys)
      OpenPath(xs).append(OpenPath(ys)).elements ?= (xs ++ ys)
    }
}
