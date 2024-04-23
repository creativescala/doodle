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

import doodle.algebra.Algebra
import doodle.algebra.Picture
import doodle.algebra.Clip
import doodle.core.ClosedPath

trait ClipSyntax {
  implicit class ClipPictureOps[Alg <: Clip, A](
      picture: Picture[Alg, A]
  ) {
    def clip(clipPath: ClosedPath): Picture[Alg, A] =
      new Picture[Alg, A] {
        def apply(implicit algebra: Alg): algebra.Drawing[A] =
          algebra.clip(picture(algebra), clipPath)
      }
  }
}
