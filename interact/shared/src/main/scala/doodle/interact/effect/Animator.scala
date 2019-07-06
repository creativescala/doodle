/*
 * Copyright 2019 noelwelsh
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
package interact
package effect

import cats.Monoid
import cats.effect.IO
import doodle.algebra.{Algebra, Picture}
import doodle.effect.Renderer
import monix.execution.Scheduler
import monix.reactive.Observable

/**
  * The `Animator` typeclass describes a data type that can render an animation
  * to a Canvas.
  */
trait Animator[Canvas] {

  /** Animate frames that are produced by an `Observable`. */
  def animate[Alg[x[_]] <: Algebra[x], F[_], A, Frame](canvas: Canvas)(
      frames: Observable[Picture[Alg, F, A]])(
      implicit e: Renderer[Alg, F, Frame, Canvas],
      s: Scheduler,
      m: Monoid[A]): IO[A]
}
object Animator {
  def apply[Canvas](implicit animator: Animator[Canvas]): Animator[Canvas] =
    animator
}
