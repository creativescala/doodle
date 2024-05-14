/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package core

import org.scalacheck.Prop.*
import org.scalacheck.*

object OpenPathSpec extends Properties("OpenPath properties") {
  import Generators.*

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
