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
package image
package syntax

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import doodle.algebra.Picture
import doodle.core.format.Format
import doodle.core.{Base64 => B64}
import doodle.effect.Base64
import doodle.effect.Writer
import doodle.image.Image
import doodle.language.Basic

import java.io.File

class JvmImageSyntax extends AbstractImageSyntax(doodle.syntax.renderer) {
  implicit class Base64ImageOps(image: Image) {
    def base64[Fmt <: Format] = new Base64Ops[Fmt](image)
  }

  /** This strange construction allows the user to write
    * `anImage.base64[AFormat]` without having to specify other, mostly
    * irrelevant to the user, type parameters.
    */
  final class Base64Ops[Fmt <: Format](image: Image) {
    def apply[Alg <: Basic, Frame](implicit
        w: Base64[Alg, Frame, Fmt],
        runtime: IORuntime
    ): B64[Fmt] = {
      val picture = new Picture[Basic, Unit] {
        def apply(implicit algebra: Basic): algebra.Drawing[Unit] =
          image.compile(algebra)
      }
      val (_, base64) = w.base64(picture).unsafeRunSync()
      base64
    }
  }

  import doodle.syntax.writer._

  implicit class ImageWriterOps(image: Image) {
    def write[Fmt <: Format] = new ImageWriterUnitOps[Fmt](image)
    def writeToIO[Fmt <: Format] = new ImageWriterIOOps[Fmt](image)
  }

  /** This strange construction allows the user to write
    * `anImage.write[AFormat](filename)` without having to specify other, mostly
    * irrelevant to the user, type parameters.
    */
  final class ImageWriterUnitOps[Fmt <: Format](image: Image) {
    def apply[Alg <: Basic, Frame](file: String)(implicit
        w: Writer[Alg, Frame, Fmt],
        r: IORuntime
    ): Unit =
      apply(new File(file))

    def apply[Alg <: Basic, Frame](file: String, frame: Frame)(implicit
        w: Writer[Alg, Frame, Fmt],
        r: IORuntime
    ): Unit =
      apply(new File(file), frame)

    def apply[Alg <: Basic, Frame](
        file: File
    )(implicit w: Writer[Alg, Frame, Fmt], r: IORuntime): Unit =
      image.compile[Alg].write[Fmt](file)

    def apply[Alg <: Basic, Frame](file: File, frame: Frame)(implicit
        w: Writer[Alg, Frame, Fmt],
        r: IORuntime
    ): Unit =
      image.compile[Alg].write[Fmt](file, frame)
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

    def apply[Alg <: Basic, Frame](file: String, frame: Frame)(implicit
        w: Writer[Alg, Frame, Fmt]
    ): IO[Unit] =
      apply(new File(file), frame)

    def apply[Alg <: Basic, Frame](
        file: File
    )(implicit w: Writer[Alg, Frame, Fmt]): IO[Unit] =
      image.compile[Alg].writeToIO[Fmt](file)

    def apply[Alg <: Basic, Frame](file: File, frame: Frame)(implicit
        w: Writer[Alg, Frame, Fmt]
    ): IO[Unit] =
      image.compile[Alg].writeToIO[Fmt](file, frame)
  }
}
