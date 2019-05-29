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
package syntax

import cats.effect.IO
import doodle.algebra.{Algebra,Picture}
import doodle.effect.{DefaultRenderer, Renderer}

trait RendererSyntax {
  implicit class RendererPictureOps[Alg[x[_]] <: Algebra[x], F[_], A](
      picture: Picture[Alg, F, A]) {
    def draw[Frame, Canvas](frame: Frame)(
        implicit renderer: Renderer[Alg, F, Frame, Canvas]): A =
      (for {
        canvas <- renderer.frame(frame)
        a <- renderer.render(canvas)(picture)
      } yield a).unsafeRunSync()

    def draw[Frame, Canvas]()(
        implicit renderer: DefaultRenderer[Alg, F, Frame, Canvas]): A =
      (for {
        canvas <- renderer.frame(renderer.default)
        a <- renderer.render(canvas)(picture)
      } yield a).unsafeRunSync()
  }

  implicit class RendererFrameOps[Frame](frame: Frame) {
    def canvas[Alg[x[_]] <: Algebra[x], F[_], Canvas]()(
      implicit renderer: Renderer[Alg, F, Frame, Canvas]): IO[Canvas] =
      renderer.frame(frame)
  }
}
