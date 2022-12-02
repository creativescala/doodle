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
package syntax

import cats.Monoid
import cats.effect.IO
import cats.effect.unsafe.IORuntime
import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.interact.effect.AnimationWriter
import fs2.Stream

import java.io.File

trait AnimationWriterSyntax {
  def animationWriterNullCallback[A](r: Either[Throwable, A]): Unit =
    r match {
      case Left(err) =>
        println("There was an error rendering an animation")
        err.printStackTrace()

      case Right(_) => ()
    }

  implicit class AnimationWriterStreamOps[Alg <: Algebra, F[_], A](
      frames: Stream[IO, Picture[Alg, A]]
  ) {

    def writeToIO[Format] =
      new AnimationWriterIOOpsHelper[Format](frames)

    def write[Format] =
      new AnimationWriterOpsHelper[Format](frames)

    class AnimationWriterIOOpsHelper[Format](
        frames: Stream[IO, Picture[Alg, A]]
    ) {
      def apply[Frame](file: String, frame: Frame)(implicit
          m: Monoid[A],
          w: AnimationWriter[Alg, F, Frame, Format]
      ): IO[A] =
        apply(new File(file), frame)

      def apply[Frame](file: File, frame: Frame)(implicit
          m: Monoid[A],
          w: AnimationWriter[Alg, F, Frame, Format]
      ): IO[A] =
        w.write(file, frame, frames)
    }

    class AnimationWriterOpsHelper[Format](
        frames: Stream[IO, Picture[Alg, A]]
    ) {
      def apply[Frame](file: String, frame: Frame)(implicit
          m: Monoid[A],
          w: AnimationWriter[Alg, F, Frame, Format],
          runtime: IORuntime
      ): Unit =
        apply(new File(file), frame)

      def apply[Frame](file: File, frame: Frame)(implicit
          m: Monoid[A],
          w: AnimationWriter[Alg, F, Frame, Format],
          runtime: IORuntime
      ): Unit =
        w.write(file, frame, frames).unsafeRunAsync(animationWriterNullCallback)
    }
  }
}
