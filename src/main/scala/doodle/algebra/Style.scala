/*
 * Copyright 2015 noelwelsh
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

/**
  * Apply styling to a image. The type `C` is the type of colors, and `A`, as
  * usual, is that of image.
  */
trait Style[F[_],C,A] {
  def fillColor(image: F[A], fillColor: C): F[A]
  def strokeColor(image: F[A], strokeColor: C): F[A]

  def noFill(image: F[A]): F[A]
  def noStroke(image: F[A]): F[A]
}
