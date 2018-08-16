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
package animation

import cats.Monoid
import cats.effect.IO
import doodle.algebra.Image
import doodle.engine.Engine
import monix.reactive.Observable

/**
  * The `Animation` typeclass describes a data type that can render an animation
  * to a Canvas.
  */
trait Animation[Canvas]{
  /** Animate frames that are contained in structure that can be `Traversed`. */
  def animateIterable[Algebra,F[_],A](canvas: Canvas)(frames: Iterable[Image[Algebra,F,A]])(implicit e: Engine[Algebra,F,Canvas], m: Monoid[A]): IO[A]

  /** Animate frames that are produced by an `Observable`. */
  def animateObservable[Algebra,F[_],A](canvas: Canvas)(frames: Observable[Image[Algebra,F,A]])(implicit e: Engine[Algebra,F,Canvas], m: Monoid[A]): IO[A]
}
object Animation {
  def apply[Canvas](implicit animation: Animation[Canvas]): Animation[Canvas] = animation
}
