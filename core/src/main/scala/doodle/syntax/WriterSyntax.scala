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
package syntax

import doodle.algebra.Image
import doodle.engine.{Frame,Writer}
import java.io.File

trait WriterSyntax {
  implicit class WriterOps[Algebra,F[_],A](image: Image[Algebra,F,A]) {
    def write[Format](file: String)(implicit w: Writer[Algebra,F,Format]): A =
      write(new File(file))

    def write[Format](file: File)(implicit w: Writer[Algebra,F,Format]): A =
      write(file, Frame.fitToImage())

    def write[Format](file: String, frame: Frame)(implicit w: Writer[Algebra,F,Format]): A =
      write(new File(file), frame)

    def write[Format](file: File, frame: Frame)(implicit w: Writer[Algebra,F,Format]): A =
      w.write(file, frame, image).unsafeRunSync()
  }
}
