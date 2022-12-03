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
package syntax

import doodle.algebra.Picture
import doodle.algebra.Style
import doodle.core.Cap
import doodle.core.Color
import doodle.core.Gradient
import doodle.core.Join

trait StyleSyntax {
  implicit class StylePictureOps[Alg <: Style, A](
      picture: Picture[Alg, A]
  ) {
    def fillColor(fillColor: Color): Picture[Alg, A] =
      new Picture[Alg, A] {
        def apply(implicit algebra: Alg): algebra.Drawing[A] =
          algebra.fillColor(picture(algebra), fillColor)
      }

    def fillGradient(fillGradient: Gradient): Picture[Alg, A] =
      new Picture[Alg, A] {
        def apply(implicit algebra: Alg): algebra.Drawing[A] =
          algebra.fillGradient(picture(algebra), fillGradient)
      }

    def strokeColor(strokeColor: Color): Picture[Alg, A] =
      new Picture[Alg, A] {
        def apply(implicit algebra: Alg): algebra.Drawing[A] =
          algebra.strokeColor(picture(algebra), strokeColor)
      }

    def strokeWidth(strokeWidth: Double): Picture[Alg, A] =
      new Picture[Alg, A] {
        def apply(implicit algebra: Alg): algebra.Drawing[A] =
          algebra.strokeWidth(picture(algebra), strokeWidth)
      }

    def strokeDash(pattern: Array[Double]): Picture[Alg, A] =
      new Picture[Alg, A] {
        def apply(implicit algebra: Alg): algebra.Drawing[A] =
          algebra.strokeDash(picture(algebra), pattern)
      }

    def strokeCap(strokeCap: Cap): Picture[Alg, A] =
      new Picture[Alg, A] {
        def apply(implicit algebra: Alg): algebra.Drawing[A] =
          algebra.strokeCap(picture(algebra), strokeCap)
      }

    def strokeJoin(strokeJoin: Join): Picture[Alg, A] =
      new Picture[Alg, A] {
        def apply(implicit algebra: Alg): algebra.Drawing[A] =
          algebra.strokeJoin(picture(algebra), strokeJoin)
      }

    def noDash: Picture[Alg, A] =
      new Picture[Alg, A] {
        def apply(implicit algebra: Alg): algebra.Drawing[A] =
          algebra.noDash(picture(algebra))
      }

    def noFill: Picture[Alg, A] =
      new Picture[Alg, A] {
        def apply(implicit algebra: Alg): algebra.Drawing[A] =
          algebra.noFill(picture(algebra))
      }

    def noStroke: Picture[Alg, A] =
      new Picture[Alg, A] {
        def apply(implicit algebra: Alg): algebra.Drawing[A] =
          algebra.noStroke(picture(algebra))
      }
  }
}
