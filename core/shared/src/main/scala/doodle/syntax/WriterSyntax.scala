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

import doodle.algebra.{Algebra,Image}
import doodle.effect.Writer
import java.io.File

trait WriterSyntax {
  implicit class WriterOps[Alg[x[_]] <: Algebra[x], F[_], A](image: Image[Alg, F, A]) {
    // This class exists solely so the user doesn't have to provide the `Frame`
    // type parameter when calling syntax methods.
    class WriterOpsHelper[Format](image: Image[Alg, F, A]) {
      def apply[Frame](file: String)(implicit w: Writer[Alg, F, Frame, Format]): A =
        apply(new File(file))

      def apply[Frame](file: File)(implicit w: Writer[Alg, F, Frame, Format]): A =
        w.write(file, image).unsafeRunSync()

      def apply[Frame](file: String, frame: Frame)(
        implicit w: Writer[Alg, F, Frame, Format]): A =
        apply(new File(file), frame)

      def apply[Frame](file: File, frame: Frame)(
        implicit w: Writer[Alg, F, Frame, Format]): A =
        w.write(file, frame, image).unsafeRunSync()
    }

    def write[Format] =
      new WriterOpsHelper(image)
  }
}
