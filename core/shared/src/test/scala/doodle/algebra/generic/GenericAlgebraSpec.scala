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
package algebra
package generic

import cats.instances.unit.*
import org.scalacheck.*
import org.scalacheck.Prop.*

object GenericAlgebraspec extends Properties("Generic algebra properties") {
  implicit val algebra: TestAlgebra = TestAlgebra()

  property("Beside doubles the width") = forAll(Generators.finalized) { f =>
    val (bbDouble, _) = algebra.beside(f, f).run(List.empty).value
    val (bb, _) = f.run(List.empty).value

    bbDouble.width ?= (bb.width * 2)
    bbDouble.height ?= bb.height
  }

  property("Above doubles the height") = forAll(Generators.finalized) { f =>
    val (bbDouble, _) = algebra.above(f, f).run(List.empty).value
    val (bb, _) = f.run(List.empty).value

    bbDouble.height ?= (bb.height * 2)
    bbDouble.width ?= bb.width
  }

  property("On doesn't change the bounding box") =
    forAll(Generators.finalized) { f =>
      val (bbDouble, _) = algebra.on(f, f).run(List.empty).value
      val (bb, _) = f.run(List.empty).value

      bbDouble.height ?= bb.height
      bbDouble.width ?= bb.width
    }
}
