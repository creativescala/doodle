/*
 * Copyright 2019 Noel Welsh
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

import cats.implicits._
import doodle.core.{Transform => Tx}
import doodle.algebra.generic.reified.Reification
import org.scalacheck._
import org.scalacheck.Prop._

object SizeSpec extends Properties("Size properties") {
  implicit val algebra = TestAlgebra()

  def getA[A](f: Finalized[Reification, A]): A = {
    val (_, rdr) = f.runA(List.empty).value
    rdr.runA(Tx.identity).value.extract
  }

  property("width matches bounding box width") =
    forAllNoShrink(Generators.finalized){ f =>
      val width = getA(algebra.width(f))
      val (bb, _) = f.runA(List.empty).value

      width ?= bb.width
    }

  property("height matches bounding box height") =
    forAllNoShrink(Generators.finalized){ f =>
      val height = getA(algebra.height(f))
      val (bb, _) = f.runA(List.empty).value

      height ?= bb.height
    }

  property("size matches bounding box size") =
    forAllNoShrink(Generators.finalized){ f =>
      val size = getA(algebra.size(f))
      val (bb, _) = f.runA(List.empty).value

      size ?= ((bb.width, bb.height))
    }
}
