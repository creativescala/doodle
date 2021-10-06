/*
 * Copyright 2015-2020 Noel Welsh
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
package java2d
package effect

import cats.Monoid
import cats.effect.IO
import doodle.effect.Renderer
import doodle.interact.effect.AnimationRenderer
import fs2.Stream

object Java2dAnimationRenderer extends AnimationRenderer[Canvas] {
  def animate[Alg[x[_]] <: doodle.algebra.Algebra[x], F[_], A, Frm](
      canvas: Canvas
  )(frames: Stream[IO, doodle.algebra.Picture[Alg, F, A]])(implicit
      e: Renderer[Alg, F, Frm, Canvas],
      m: Monoid[A]
  ): IO[A] =
    frames
      .evalMap(e.render(canvas))
      .compile
      .foldMonoid
}
