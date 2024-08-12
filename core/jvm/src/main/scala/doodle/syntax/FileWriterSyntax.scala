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
import cats.effect.unsafe.IORuntime
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.core.format.Format
import doodle.effect.DefaultFrame
import doodle.effect.FileWriter

import java.io.File

trait FileWriterSyntax {
  implicit class WriterOps[Alg <: Algebra, A](
      picture: Picture[Alg, A]
  ) {
    // This class exists solely so the user doesn't have to provide the `Frame`
    // type parameter when calling syntax methods.
    class FileWriterOpsHelper[Fmt <: Format](picture: Picture[Alg, A]) {
      def apply[Frame](file: String)(implicit
          w: FileWriter[Alg, Frame, Fmt],
          f: DefaultFrame[Frame],
          r: IORuntime
      ): A =
        apply(new File(file))

      def apply[Frame](file: File)(implicit
          w: FileWriter[Alg, Frame, Fmt],
          f: DefaultFrame[Frame],
          r: IORuntime
      ): A =
        w.write(file, picture).unsafeRunSync()

      def apply[Frame](file: String, frame: Frame)(implicit
          w: FileWriter[Alg, Frame, Fmt],
          r: IORuntime
      ): A =
        apply(new File(file), frame)

      def apply[Frame](file: File, frame: Frame)(implicit
          w: FileWriter[Alg, Frame, Fmt],
          r: IORuntime
      ): A =
        w.write(file, frame, picture).unsafeRunSync()
    }

    class FileWriterIOOpsHelper[Fmt <: Format](picture: Picture[Alg, A]) {
      def apply[Frame](file: String)(implicit
          w: FileWriter[Alg, Frame, Fmt],
          f: DefaultFrame[Frame]
      ): IO[A] =
        apply(new File(file))

      def apply[Frame](file: File)(implicit
          w: FileWriter[Alg, Frame, Fmt],
          f: DefaultFrame[Frame]
      ): IO[A] =
        w.write(file, picture)

      def apply[Frame](file: String, frame: Frame)(implicit
          w: FileWriter[Alg, Frame, Fmt]
      ): IO[A] =
        apply(new File(file), frame)

      def apply[Frame](file: File, frame: Frame)(implicit
          w: FileWriter[Alg, Frame, Fmt]
      ): IO[A] =
        w.write(file, frame, picture)
    }

    def write[Fmt <: Format] =
      new FileWriterOpsHelper[Fmt](picture)

    def writeToIO[Fmt <: Format] =
      new FileWriterIOOpsHelper[Fmt](picture)
  }
}
