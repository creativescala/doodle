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

import cats.Semigroup
import doodle.core.Angle
import doodle.core.Landmark
import doodle.core.Point
import doodle.core.Vec

trait Layout[F[_]] extends Algebra[F] {
  def on[A](top: F[A], bottom: F[A])(implicit s: Semigroup[A]): F[A]
  def beside[A](left: F[A], right: F[A])(implicit s: Semigroup[A]): F[A]
  def above[A](top: F[A], bottom: F[A])(implicit s: Semigroup[A]): F[A]
  def at[A](img: F[A], landmark: Landmark): F[A]
  def margin[A](
      img: F[A],
      top: Double,
      right: Double,
      bottom: Double,
      left: Double
  ): F[A]

  // Derived methods

  def under[A](bottom: F[A], top: F[A])(implicit s: Semigroup[A]): F[A] =
    on(top, bottom)
  def below[A](bottom: F[A], top: F[A])(implicit s: Semigroup[A]): F[A] =
    above(top, bottom)

  def at[A](img: F[A], x: Double, y: Double): F[A] =
    at(img, Landmark.point(x, y))
  def at[A](img: F[A], r: Double, a: Angle): F[A] = {
    val offset = Point(r, a)
    at(img, offset.x, offset.y)
  }
  def at[A](img: F[A], offset: Vec): F[A] =
    at(img, offset.x, offset.y)
  def at[A](img: F[A], offset: Point): F[A] =
    at(img, offset.x, offset.y)

  def margin[A](img: F[A], width: Double, height: Double): F[A] =
    margin(img, height, width, height, width)
  def margin[A](img: F[A], width: Double): F[A] =
    margin(img, width, width, width, width)
}
