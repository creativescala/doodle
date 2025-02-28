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
import doodle.algebra.Debug
import doodle.algebra.Picture
import doodle.core.Color

trait DebugSyntax {
  implicit class DebugPictureOps[Alg <: Algebra, A](
      picture: Picture[Alg, A]
  ) {

    /** Draw bounding box and origin in the given color on top of the given
      * picture.
      */
    def debug(color: Color): Picture[Alg with Debug, A] =
      new Picture[Alg with Debug, A] {
        def apply(implicit algebra: Alg with Debug): algebra.Drawing[A] =
          algebra.debug(picture(algebra), color)
      }

    /** Draw bounding box and origin in crimson on top of the given picture.
      */
    def debug: Picture[Alg with Debug, A] =
      debug(Color.crimson)
  }
}
