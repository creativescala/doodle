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
package image
package syntax

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import doodle.algebra.Picture
import doodle.core.format.Format
import doodle.effect.DefaultRenderer
import doodle.effect.Renderer
import doodle.effect.Writer
import doodle.image.Image
import doodle.language.Basic

import java.io.File

trait ImageSyntax {
  def unitCallback[A]: Either[Throwable, A] => Unit =
    (either) =>
      either match {
        case Left(err) =>
          println("There was an error working with an Image")
          err.printStackTrace()

        case Right(_) => ()
      }

  implicit class ImageOps(image: Image) {
    def draw[Alg <: Basic, Frame, Canvas](
        frame: Frame
    )(implicit renderer: Renderer[Alg, Frame, Canvas], r: IORuntime): Unit =
      (for {
        canvas <- renderer.canvas(frame)
        a <- renderer.render(canvas)(
          new Picture[Alg, Unit] {
            def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
              image.compile(algebra)
          }
        )
      } yield a).unsafeRunAsync(unitCallback)

    def draw[Alg <: Basic, Frame, Canvas]()(implicit
        renderer: DefaultRenderer[Alg, Frame, Canvas],
        r: IORuntime
    ): Unit =
      (for {
        canvas <- renderer.canvas(renderer.default)
        a <- renderer.render(canvas)(
          new Picture[Alg, Unit] {
            def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
              image.compile(algebra)
          }
        )
      } yield a).unsafeRunAsync(unitCallback)

    def write[Fmt <: Format] = new ImageWriterOps[Fmt](image)
    def writeToIO[Fmt <: Format] = new ImageWriterIOOps[Fmt](image)
  }

  /** This strange construction allows the user to write
    * `anImage.write[AFormat](filename)` without having to specify other, mostly
    * irrelevant to the user, type parameters.
    */
  final class ImageWriterOps[Fmt <: Format](image: Image) {
    def apply[Alg <: Basic, Frame](file: String)(implicit
        w: Writer[Alg, Frame, Fmt],
        r: IORuntime
    ): Unit =
      apply(new File(file))

    def apply[Alg <: Basic, Frame](
        file: File
    )(implicit w: Writer[Alg, Frame, Fmt], r: IORuntime): Unit =
      w.write(
        file,
        new Picture[Alg, Unit] {
          def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
            image.compile(algebra)
        }
      ).unsafeRunAsync(unitCallback)

    def apply[Alg <: Basic, Frame](file: String, frame: Frame)(implicit
        w: Writer[Alg, Frame, Fmt],
        r: IORuntime
    ): Unit =
      apply(new File(file), frame)

    def apply[Alg <: Basic, Frame](file: File, frame: Frame)(implicit
        w: Writer[Alg, Frame, Fmt],
        r: IORuntime
    ): Unit =
      w.write(
        file,
        frame,
        new Picture[Alg, Unit] {
          def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
            image.compile(algebra)
        }
      ).unsafeRunAsync(unitCallback)
  }

  /** This strange construction allows the user to write
    * `anImage.write[AFormat](filename)` without having to specify other, mostly
    * irrelevant to the user, type parameters.
    */
  final class ImageWriterIOOps[Fmt <: Format](image: Image) {
    def apply[Alg <: Basic, Frame](file: String)(implicit
        w: Writer[Alg, Frame, Fmt]
    ): IO[Unit] =
      apply(new File(file))

    def apply[Alg <: Basic, Frame](
        file: File
    )(implicit w: Writer[Alg, Frame, Fmt]): IO[Unit] =
      w.write(
        file,
        new Picture[Alg, Unit] {
          def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
            image.compile(algebra)
        }
      )

    def apply[Alg <: Basic, Frame](file: String, frame: Frame)(implicit
        w: Writer[Alg, Frame, Fmt]
    ): IO[Unit] =
      apply(new File(file), frame)

    def apply[Alg <: Basic, Frame](file: File, frame: Frame)(implicit
        w: Writer[Alg, Frame, Fmt]
    ): IO[Unit] =
      w.write(
        file,
        frame,
        new Picture[Alg, Unit] {
          def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
            image.compile(algebra)
        }
      )
  }
}
