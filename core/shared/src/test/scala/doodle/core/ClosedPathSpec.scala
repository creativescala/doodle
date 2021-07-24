package doodle
package core

import org.scalacheck.Prop._
import org.scalacheck._

object ClosedPathSpec extends Properties("ClosedPath properties") {
  import Generators._

  property("added element is last in the list of elements") =
    forAll(Gen.listOf(pathElement), pathElement) { (elts, elt) =>
      ClosedPath(elts).add(elt).elements ?= (elts ++ List(elt))
    }

  property("appended list comes after original elements") =
    forAll(Gen.listOf(pathElement), Gen.listOf(pathElement)) { (xs, ys) =>
      ClosedPath(xs).append(ys).elements ?= (xs ++ ys)
      ClosedPath(xs).append(ClosedPath(ys)).elements ?= (xs ++ ys)
    }
}
