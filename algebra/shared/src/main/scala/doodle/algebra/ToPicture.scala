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
package algebra

import doodle.core.ClosedPath
import doodle.core.OpenPath

/** Represents converting from the Input type to a Picture, and depends on the
  * support of some Algebra to actually do the conversion. This can be used to
  * represent, for example, creating a picture from a bitmap in a base64 encoded
  * string.
  */
trait ToPicture[Input, Alg <: Algebra] {
  def toPicture(in: Input): Picture[Alg, Unit]
}
object ToPicture {
  given [Alg <: Path]: ToPicture[OpenPath, Alg] with {
    def toPicture(in: OpenPath): Picture[Alg, Unit] =
      new Picture[Alg, Unit] {
        def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
          algebra.path(in)
      }
  }

  given [Alg <: Path]: ToPicture[ClosedPath, Alg] with {
    def toPicture(in: ClosedPath): Picture[Alg, Unit] =
      new Picture[Alg, Unit] {
        def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
          algebra.path(in)
      }
  }
}
