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
package interact
package effect

import cats.Monoid
import cats.effect.IO
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.effect.Renderer
import fs2.Stream

/** The `AnimationRenderer` typeclass describes a data type that can render an
  * animation to a Canvas.
  */
trait AnimationRenderer[Canvas] {

  /** Animate frames that are produced by a `Stream`. */
  def animate[Alg <: Algebra, F[_], A, Frame](
      canvas: Canvas
  )(frames: Stream[IO, Picture[Alg, A]])(implicit
      e: Renderer[Alg, Frame, Canvas],
      m: Monoid[A]
  ): IO[A]
}
object AnimationRenderer {
  def apply[Canvas](implicit
      animator: AnimationRenderer[Canvas]
  ): AnimationRenderer[Canvas] =
    animator
}
