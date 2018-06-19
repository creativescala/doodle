/*
 * Copyright 2015 noelwelsh
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

import org.scalacheck._

trait Generators extends doodle.core.Generators {
  val width: Gen[Double] = Gen.posNum[Double]
  val height = width

  def shape(shape: TestAlgebra): Gen[Finalized[TestGraphicsContext.Log, Unit]] =
    Gen.oneOf(
      Gen.zip(width, height).map{ case (w, h) => shape.rectangle(w,h) },
      width.map(w => shape.square(w)),
      Gen.zip(width, height).map{ case (w, h) => shape.triangle(w,h) },
      width.map(w => shape.circle(w)),
      Gen.const(shape.empty)
    )

  val finalized: Gen[Finalized[TestGraphicsContext.Log,Unit]] =
    shape(TestAlgebra())
}
object Generators extends Generators
