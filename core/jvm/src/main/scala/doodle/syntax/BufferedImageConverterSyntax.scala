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
import doodle.effect.BufferedImageConverter
import doodle.core.format.Format
import java.awt.image.BufferedImage

trait BufferedImageConverterSyntax {
  implicit class BufferedImageConverterOps[Alg <: Algebra, A](
      picture: Picture[Alg, A]
  ) {
    class BufferedImageConverterOpsHelper[Fmt <: Format](
        picture: Picture[Alg, A]
    ) {
      def apply[Frame](frame: Frame)(implicit
          w: BufferedImageConverter[Alg, Frame, Fmt],
          r: IORuntime
      ): (A, BufferedImage) =
        w.bufferedImage(frame, picture).unsafeRunSync()
    }

    class BufferedImageConverterIOOpsHelper[Fmt <: Format](
        picture: Picture[Alg, A]
    ) {
      def apply[Frame](frame: Frame)(implicit
          w: BufferedImageConverter[Alg, Frame, Fmt],
          r: IORuntime
      ): IO[(A, BufferedImage)] =
        w.bufferedImage(frame, picture)
    }

    def bufferedImage[Fmt <: Format] =
      new BufferedImageConverterOpsHelper[Fmt](picture)

    def bufferedImageToIO[Fmt <: Format] =
      new BufferedImageConverterIOOpsHelper[Fmt](picture)
  }
}
