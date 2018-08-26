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

/** Higher level shape primitives. These draw common geometric shapes with the
  * center of the shape the origin of the bounding box. */
trait Shape[F[_]] {
  /** A rectangle with the given width and height. */
  def rectangle(width: Double, height: Double): F[Unit]
  /** A square with the given side length. */
  def square(width: Double): F[Unit]
  /** An isoceles triangle with the given width and height. */
  def triangle(width: Double, height: Double): F[Unit]
  /** A circle with the given diameter. We use diamter rather than radius so
    * circle(100) has the same size as square(100) */
  def circle(diameter: Double): F[Unit]
  /** The empty shape, which is no shape at all. */
  def empty: F[Unit]
}
