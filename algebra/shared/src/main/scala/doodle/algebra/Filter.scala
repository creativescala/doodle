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
import doodle.core.Normalized

trait Filter extends Algebra {

  def gaussianBlur[A](picture: Drawing[A], stdDeviation: Double): Drawing[A]

  def boxBlur[A](picture: Drawing[A], radius: Int): Drawing[A]

  def detectEdges[A](picture: Drawing[A]): Drawing[A]

  def sharpen[A](picture: Drawing[A], amount: Double): Drawing[A]

  def emboss[A](picture: Drawing[A]): Drawing[A]

  /** Apply a custom convolution kernel
    *
    * @param kernel
    *   The convolution kernel
    * @param divisor
    *   Optional divisor for the result (defaults to sum of kernel)
    * @param bias
    *   Optional bias to add to each pixel
    */
  def convolveMatrix[A](
      picture: Drawing[A],
      kernel: Kernel,
      divisor: Option[Double] = None,
      bias: Double = 0.0
  ): Drawing[A]

  def dropShadow[A](
      picture: Drawing[A],
      offsetX: Double = 4.0,
      offsetY: Double = 4.0,
      blur: Double = 4.0,
      color: Color = Color.black.alpha(Normalized(0.5))
  ): Drawing[A]
}

/** Companion object with standard convolution kernels */
object Filter {

  val gaussianKernel3x3: Kernel = Kernel(
    3,
    3,
    IArray(
      1.0 / 16,
      2.0 / 16,
      1.0 / 16,
      2.0 / 16,
      4.0 / 16,
      2.0 / 16,
      1.0 / 16,
      2.0 / 16,
      1.0 / 16
    )
  )

  val edgeDetectionKernel: Kernel = Kernel(
    3,
    3,
    IArray(
      0.0, -1.0, 0.0, -1.0, 4.0, -1.0, 0.0, -1.0, 0.0
    )
  )

  val sharpenKernel: Kernel = Kernel(
    3,
    3,
    IArray(
      0.0, -1.0, 0.0, -1.0, 5.0, -1.0, 0.0, -1.0, 0.0
    )
  )

  val embossKernel: Kernel = Kernel(
    3,
    3,
    IArray(
      -2.0, -1.0, 0.0, -1.0, 1.0, 1.0, 0.0, 1.0, 2.0
    )
  )

  val boxBlurKernel3x3: Kernel = Kernel(3, 3, IArray.fill(9)(1.0 / 9))
}

// @todo might be interesting to have a Kernel composition
// object ConvolutionKernel {
//   implicit class KernelOps(kernel: ConvolutionKernel) {
//     def *(scalar: Double): ConvolutionKernel
//     def +(other: ConvolutionKernel): ConvolutionKernel
//   }
// }

trait FilterConstructor extends BaseConstructor {
  // no constructors for Filter - it only has combinators
}
