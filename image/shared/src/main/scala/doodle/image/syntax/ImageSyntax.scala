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
import doodle.language.Basic
import java.io.File

trait ImageSyntax {
  implicit class ImageOps(image: Image) {
    def draw[Algebra[A[?]] <: Basic[A[?]], F[_], Frame, Canvas](frame: Frame)(
        implicit renderer: Renderer[Algebra[F], F, Frame, Canvas]): Unit =
      (for {
        canvas <- renderer.frame(frame)
        a <- renderer.render(canvas)(algebra => image.compile(algebra))
      } yield a).unsafeRunSync()

    def draw[Algebra[A[?]] <: Basic[A[?]], F[_], Frame, Canvas]()(
        implicit renderer: DefaultRenderer[Algebra[F], F, Frame, Canvas]): Unit =
      (for {
        canvas <- renderer.frame(renderer.default)
        a <- renderer.render(canvas)(algebra => image.compile(algebra))
      } yield a).unsafeRunSync()

    def write[Format] = new ImageWriterOps[Format](image)
  }

  /** This strange construction allows the user to write `anImage.write[AFormat](filename)`
    * without having to specify other, mostly irrelevant to the user, type parameters. */
  final class ImageWriterOps[Format](image: Image) {
    def apply[Algebra[A[?]] <: Basic[A[?]], F[_], Frame](file: String)(
        implicit w: Writer[Algebra[F], F, Frame, Format]): Unit =
      apply(new File(file))

    def apply[Algebra[A[?]] <: Basic[A[?]], F[_], Frame](file: File)(
        implicit w: Writer[Algebra[F], F, Frame, Format]): Unit =
      w.write(file,
              doodle.algebra.Image((algebra: Algebra[F]) => image.compile(algebra))).unsafeRunSync()

    def apply[Algebra[A[?]] <: Basic[A[?]], F[_], Frame](file: String, frame: Frame)(
        implicit w: Writer[Algebra[F], F, Frame, Format]): Unit =
      apply(new File(file), frame)

    def apply[Algebra[A[?]] <: Basic[A[?]], F[_], Frame](file: File, frame: Frame)(
        implicit w: Writer[Algebra[F], F, Frame, Format]): Unit =
      w.write(
          file,
          frame,
          doodle.algebra.Image((algebra: Algebra[F]) => image.compile(algebra)))
        .unsafeRunSync()
  }
}
