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
import doodle.core.Base64 as B64
import doodle.core.format.Format
import doodle.effect.Base64Writer
import doodle.effect.DefaultFrame

trait Base64WriterSyntax {
  implicit class Base64Ops[Alg <: Algebra, A](
      picture: Picture[Alg, A]
  ) {
    // This class exists solely so the user doesn't have to provide the `Frame`
    // type parameter when calling syntax methods.
    class Base64OpsHelper[Fmt <: Format](picture: Picture[Alg, A]) {
      def apply[Frame]()(implicit
          w: Base64Writer[Alg, Frame, Fmt],
          f: DefaultFrame[Frame],
          r: IORuntime
      ): (A, B64[Fmt]) =
        w.base64(picture).unsafeRunSync()

      def apply[Frame](frame: Frame)(implicit
          w: Base64Writer[Alg, Frame, Fmt],
          r: IORuntime
      ): (A, B64[Fmt]) =
        w.base64(frame, picture).unsafeRunSync()
    }

    class Base64IOOpsHelper[Fmt <: Format](picture: Picture[Alg, A]) {
      def apply[Frame]()(implicit
          w: Base64Writer[Alg, Frame, Fmt],
          f: DefaultFrame[Frame]
      ): IO[(A, B64[Fmt])] =
        w.base64(picture)

      def apply[Frame](frame: Frame)(implicit
          w: Base64Writer[Alg, Frame, Fmt]
      ): IO[(A, B64[Fmt])] =
        w.base64(frame, picture)
    }

    def base64[Fmt <: Format] =
      new Base64OpsHelper[Fmt](picture)

    def base64ToIO[Fmt <: Format] =
      new Base64IOOpsHelper[Fmt](picture)
  }
}
