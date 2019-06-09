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
package image
package syntax

import doodle.effect.{DefaultRenderer, Renderer, Writer}
import doodle.image.Image
import doodle.algebra.Picture
import doodle.language.Basic
import java.io.File

trait ImageSyntax {
  implicit class ImageOps(image: Image) {
    def draw[Alg[x[_]] <: Basic[x], F[_], Frame, Canvas](frame: Frame)(
        implicit renderer: Renderer[Alg, F, Frame, Canvas]): Unit =
      (for {
        canvas <- renderer.canvas(frame)
        a <- renderer.render(canvas)(Picture(algebra => image.compile(algebra)))
      } yield a).unsafeRunSync()

    def draw[Alg[x[_]] <: Basic[x], F[_], Frame, Canvas]()(
        implicit renderer: DefaultRenderer[Alg, F, Frame, Canvas]): Unit =
      (for {
        canvas <- renderer.canvas(renderer.default)
        a <- renderer.render(canvas)(Picture(algebra => image.compile(algebra)))
      } yield a).unsafeRunSync()

    def write[Format] = new ImageWriterOps[Format](image)
  }

  /** This strange construction allows the user to write `anImage.write[AFormat](filename)`
    * without having to specify other, mostly irrelevant to the user, type parameters. */
  final class ImageWriterOps[Format](image: Image) {
    def apply[Alg[x[_]] <: Basic[x], F[_], Frame](file: String)(
        implicit w: Writer[Alg, F, Frame, Format]): Unit =
      apply(new File(file))

    def apply[Alg[x[_]] <: Basic[x], F[_], Frame](file: File)(
        implicit w: Writer[Alg, F, Frame, Format]): Unit =
      w.write(file,
              Picture((algebra: Alg[F]) => image.compile(algebra))).unsafeRunSync()

    def apply[Alg[x[_]] <: Basic[x], F[_], Frame](file: String, frame: Frame)(
        implicit w: Writer[Alg, F, Frame, Format]): Unit =
      apply(new File(file), frame)

    def apply[Alg[x[_]] <: Basic[x], F[_], Frame](file: File, frame: Frame)(
        implicit w: Writer[Alg, F, Frame, Format]): Unit =
      w.write(
          file,
          frame,
          Picture((algebra: Alg[F]) => image.compile(algebra)))
        .unsafeRunSync()
  }
}
