/*
 * Copyright 2015 Noel Welsh
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

import org.scalacheck.Prop._
import org.scalacheck._

object TextSpec extends Properties("Text properties") {
  val algebra = TestAlgebra()

  property("strokeColor is preserved for text") = forAll(Generators.color) {
    (c) =>
      import doodle.algebra.generic.reified.Reified._
      val reified =
        Generators.reify(algebra.strokeColor(algebra.text("Hello"), c))
      reified match {
        case List(Text(_, _, stroke, _, text)) =>
          (stroke.get.color ?= c) && (text ?= "Hello")
        case _ => Prop.falsified
      }
  }
}
