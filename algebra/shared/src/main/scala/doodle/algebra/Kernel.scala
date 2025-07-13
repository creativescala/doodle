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

/** Represents a convolution kernel/matrix for image filtering operations.
  *
  * @param width
  *   the width of the kernel (must be odd and positive)
  * @param height
  *   the height of the kernel (must be odd and positive)
  * @param elements
  *   the kernel elements in row-major order
  */
final case class Kernel(width: Int, height: Int, elements: IArray[Double]) {
  require(
    width > 0 && width % 2 == 1,
    s"Kernel width must be odd and positive, got $width"
  )
  require(
    height > 0 && height % 2 == 1,
    s"Kernel height must be odd and positive, got $height"
  )
  require(
    elements.length == width * height,
    s"Elements array length (${elements.length}) must equal width * height ($width * $height = ${width * height})"
  )

  def sum: Double = elements.sum
}
