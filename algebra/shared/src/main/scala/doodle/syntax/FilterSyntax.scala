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
import doodle.algebra.Filter
import doodle.algebra.Kernel
import doodle.algebra.Picture
import doodle.core.Color
import doodle.core.Normalized

trait FilterSyntax {
  implicit class FilterPictureOps[Alg <: Algebra, A](
      picture: Picture[Alg, A]
  ) {

    def blur(stdDeviation: Double = 3.0): Picture[Alg with Filter, A] =
      new Picture[Alg with Filter, A] {
        def apply(implicit algebra: Alg with Filter): algebra.Drawing[A] =
          algebra.gaussianBlur(picture(algebra), stdDeviation)
      }

    def gaussianBlur(stdDeviation: Double): Picture[Alg with Filter, A] =
      blur(stdDeviation)

    def boxBlur(radius: Int = 1): Picture[Alg with Filter, A] =
      new Picture[Alg with Filter, A] {
        def apply(implicit algebra: Alg with Filter): algebra.Drawing[A] =
          algebra.boxBlur(picture(algebra), radius)
      }

    def detectEdges: Picture[Alg with Filter, A] =
      new Picture[Alg with Filter, A] {
        def apply(implicit algebra: Alg with Filter): algebra.Drawing[A] =
          algebra.detectEdges(picture(algebra))
      }

    def sharpen(amount: Double = 1.0): Picture[Alg with Filter, A] =
      new Picture[Alg with Filter, A] {
        def apply(implicit algebra: Alg with Filter): algebra.Drawing[A] =
          algebra.sharpen(picture(algebra), amount)
      }

    def emboss: Picture[Alg with Filter, A] =
      new Picture[Alg with Filter, A] {
        def apply(implicit algebra: Alg with Filter): algebra.Drawing[A] =
          algebra.emboss(picture(algebra))
      }

    def convolve(
        kernel: Kernel,
        divisor: Option[Double] = None,
        bias: Double = 0.0
    ): Picture[Alg with Filter, A] =
      new Picture[Alg with Filter, A] {
        def apply(implicit algebra: Alg with Filter): algebra.Drawing[A] =
          algebra.convolveMatrix(picture(algebra), kernel, divisor, bias)
      }

    def dropShadow(
        offsetX: Double = 4.0,
        offsetY: Double = 4.0,
        blur: Double = 4.0,
        color: Color = Color.black.alpha(Normalized(0.5))
    ): Picture[Alg with Filter, A] =
      new Picture[Alg with Filter, A] {
        def apply(implicit algebra: Alg with Filter): algebra.Drawing[A] =
          algebra.dropShadow(picture(algebra), offsetX, offsetY, blur, color)
      }
  }
}
