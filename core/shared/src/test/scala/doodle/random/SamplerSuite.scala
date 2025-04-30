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

package doodle.random

import munit.FunSuite

class SamplerSuite extends FunSuite {
  val rng = scala.util.Random

  test("Sampler.cyclic produces items in expected order") {
    val samples = Sampler.cyclic(1, 2, 3, 4)(rng).getN(10)
    assertEquals(samples, Seq(1, 2, 3, 4, 1, 2, 3, 4, 1, 2))
  }

  test("Sampler.random produces all allowed items") {
    val samples = Sampler.random(1, 2, 3, 4)(rng).getN(100).distinct.sorted
    assertEquals(samples, Seq(1, 2, 3, 4))
  }

  test("Sampler.noRepeats does not repeat previous sample") {
    val samples = Sampler.noRepeats(1, 2, 3, 4)(rng).getN(100).sliding(2)
    samples.foreach { case Seq(a, b) => assertNotEquals(a, b) }
  }
}
