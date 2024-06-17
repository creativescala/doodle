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

package doodle.examples

import doodle.algebra.Layout
import doodle.algebra.Picture
import doodle.syntax.all.*

/** A small utility for writing backend agnostic examples.
  *
  *   - Alg is the Algebra needed by your examples
  *   - allPictures should be instantiated to contain all your examples
  *   - all will be a `Picture` that lays out all your examples in a vertical
  *     strip
  */
trait BaseExamples[Alg <: Layout] {
  def allPictures: Seq[Picture[Alg, Unit]]

  def all: Picture[Alg, Unit] =
    allPictures.tail
      .foldLeft(allPictures.head) { (accum, elt) =>
        accum.above(elt.margin(20, 0, 0, 0))
      }
      .margin(20)
}
