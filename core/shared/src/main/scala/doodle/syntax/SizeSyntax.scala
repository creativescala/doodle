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

import doodle.algebra.Picture
import doodle.algebra.Size
import doodle.core.BoundingBox

trait SizeSyntax {
  implicit class SizePictureOps[Alg <: Size, A](
      picture: Picture[Alg, A]
  ) {
    def boundingBox: Picture[Alg, BoundingBox] =
      new Picture[Alg, BoundingBox] {
        def apply(implicit algebra: Alg): algebra.Drawing[BoundingBox] =
          algebra.boundingBox(picture(algebra))
      }

    def height: Picture[Alg, Double] =
      new Picture[Alg, Double] {
        def apply(implicit algebra: Alg): algebra.Drawing[Double] =
          algebra.height(picture(algebra))
      }

    def width: Picture[Alg, Double] =
      new Picture[Alg, Double] {
        def apply(implicit algebra: Alg): algebra.Drawing[Double] =
          algebra.width(picture(algebra))
      }

    def size: Picture[Alg, (Double, Double)] =
      new Picture[Alg, (Double, Double)] {
        def apply(implicit algebra: Alg): algebra.Drawing[(Double, Double)] =
          algebra.size(picture(algebra))
      }
  }
}
