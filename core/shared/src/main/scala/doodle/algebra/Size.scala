/*
 * Copyright 2015-2020 Noel Welsh
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

import doodle.core.BoundingBox

/**
  * Get information about the size of the bounding box enclosing an picture.
  */
trait Size[F[_]] extends Algebra[F] {
  /**
   * Get the height of the bounding box enclosing the picture
   */
  def height[A](picture: F[A]): F[Double]

  /**
   * Get the width of the bounding box enclosing the picture
   */
  def width[A](picture: F[A]): F[Double]

  /**
   * Get the width and height of the bounding box enclosing the picture
   */
  def size[A](picture: F[A]): F[(Double,Double)]

  /**
   * Get the bounding box enclosing the picture
   */
  def boundingBox[A](picture: F[A]): F[BoundingBox]
}
