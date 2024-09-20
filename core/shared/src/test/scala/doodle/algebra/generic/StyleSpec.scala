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

import org.scalacheck.*
import org.scalacheck.Prop.*

object StyleSpec extends Properties("Style properties") {
  val style = TestAlgebra()

  property("last fillColor takes effect") =
    forAll(Generators.finalized, Generators.color) { (f, c) =>
      import doodle.algebra.generic.reified.Reified.*
      val reified = Generators.reify(style.fillColor(f, c))
      reified.foldLeft(true: Prop) { (prop, elt) =>
        prop && (elt match {
          case FillOpenPath(_, fill, _) => (
            fill.asInstanceOf[Fill.ColorFill].color ?= c
          )
          case FillClosedPath(_, fill, _) => (
            fill.asInstanceOf[Fill.ColorFill].color ?= c
          )
          case FillCircle(_, fill, _) => (
            fill.asInstanceOf[Fill.ColorFill].color ?= c
          )
          case FillRect(_, fill, _, _) => (
            fill.asInstanceOf[Fill.ColorFill].color ?= c
          )
          case FillPolygon(_, fill, _) => (
            fill.asInstanceOf[Fill.ColorFill].color ?= c
          )
          case _ => true
        })
      }
    }
}
