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

import doodle.core.Color
import doodle.core.syntax.all.ToNormalizedOps // @fixme

/** Algebra for applying filter effects to pictures.
  *
  * This includes convolution operations (blur, sharpen, edge detection)
  * and other filter effects. Different backends implement these differently:
  * - SVG: uses native filter elements like feGaussianBlur and feConvolveMatrix
  * - Java2d: may need to rasterize to bitmap first
  * - Canvas: manual pixel manipulation
  */
trait Filter extends Algebra {

    def gaussianBlur[A](picture: Drawing[A], stdDeviation: Double): Drawing[A]

    def boxBlur[A](picture: Drawing[A], radius: Int): Drawing[A]

    def detectEdges[A](picture: Drawing[A]): Drawing[A]

    def sharpen[A](picture: Drawing[A], amount: Double): Drawing[A]

    def emboss[A](picture: Drawing[A]): Drawing[A]

    /** Apply a custom convolution matrix
        *
        * @param matrix The convolution matrix (must be square and odd-sized)
        * @param divisor Optional divisor for the result (defaults to sum of matrix)
        * @param bias Optional bias to add to each pixel
        */
    def convolveMatrix[A](
        picture: Drawing[A],
        matrix: Vector[Vector[Double]],
        divisor: Option[Double] = None,
        bias: Double = 0.0
    ): Drawing[A]

    def dropShadow[A](
        picture: Drawing[A],
        offsetX: Double = 4.0,
        offsetY: Double = 4.0,
        blur: Double = 4.0,
        color: Color = Color.black.alpha((0.5).normalized)
    ): Drawing[A]
}

/** Companion object with standard convolution kernels */
object Filter {

    val gaussianKernel3x3: Vector[Vector[Double]] = Vector(
        Vector(1, 2, 1),
        Vector(2, 4, 2),
        Vector(1, 2, 1)
    ).map(_.map(_.toDouble / 16))

    val edgeDetectionKernel: Vector[Vector[Double]] = Vector(
        Vector(0, -1, 0),
        Vector(-1, 4, -1),
        Vector(0, -1, 0)
    ).map(_.map(_.toDouble))

    val sharpenKernel: Vector[Vector[Double]] = Vector(
        Vector(0, -1, 0),
        Vector(-1, 5, -1),
        Vector(0, -1, 0)
    ).map(_.map(_.toDouble))

    val embossKernel: Vector[Vector[Double]] = Vector(
        Vector(-2, -1, 0),
        Vector(-1, 1, 1),
        Vector(0, 1, 2)
    ).map(_.map(_.toDouble))

    val boxBlurKernel3x3: Vector[Vector[Double]] = Vector(
        Vector(1, 1, 1),
        Vector(1, 1, 1),
        Vector(1, 1, 1)
    ).map(_.map(_.toDouble / 9))
}

trait FilterConstructor extends BaseConstructor {
    // no constructors for Filter - it only has combinators
}
