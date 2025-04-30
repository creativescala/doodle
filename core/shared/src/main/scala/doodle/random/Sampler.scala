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

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.Random

/** A Sampler is a possibly stateful process that indefinitely produces values
  * of type `A`
  */
trait Sampler[A] {

  /** Sample an element from this sampler */
  def get(): A

  /** Sequentially sample n elements from this sampler */
  def getN(n: Int)(using ClassTag[A]): IndexedSeq[A] = {
    mutable.ArraySeq.tabulate(n)(_ => this.get()).toIndexedSeq
  }
}
type SamplerBuilder[A] = Random => Sampler[A]
object Sampler {

  /** Create a sampler that chooses an element at random. Equivalent to
    * `Random.oneOf`.
    */
  def random[A](elt: A, elts: A*): SamplerBuilder[A] =
    rng =>
      new Sampler[A] {
        private val elements = elt +: elts
        private val length = elements.size

        def get(): A =
          elements(rng.nextInt(length))
      }

  /** Create a sampler that cycles through the given elements in order. */
  def cyclic[A](elt: A, elts: A*): SamplerBuilder[A] =
    rng =>
      new Sampler[A] {
        private val elements = elt +: elts
        private val length = elements.size
        private var index = 0

        def get(): A = {
          if index == length then index = 0
          val sample = elements(index)
          index = index + 1

          sample
        }
      }

  /** Create a sampler that chooses an element at random and never repeats the
    * same choice twice in a row.
    */
  def noRepeats[A](elt0: A, elt1: A, elts: A*): SamplerBuilder[A] =
    rng =>
      new Sampler[A] {
        private val elements = elt0 +: elt1 +: elts
        private val length = elements.size
        private var previous = 0

        def get(): A = {
          val index = rng.nextInt(length - 1)
          val selected = if index < previous then index else index + 1
          previous = selected

          elements(selected)
        }
      }
}
