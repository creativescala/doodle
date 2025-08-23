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

/** Higher level shape primitives. These draw common geometric shapes with the
  * center of the shape the origin of the bounding box.
  */
trait Shape extends Algebra {
  /*

ShapeConstructor -> Shape ->                    ShapeApi
Pict => Pict       Drawing => Drawing           Renderable => SvgResult

  type Drawing[A] = doodle.algebra.generic.Finalized[Reification, A]
  type Renderable[A] = doodle.algebra.generic.Renderable[Reification, A]
   * */
  def link(
      bits: Drawing[Unit],
      href: String
  ): Drawing[Unit]

  /** A rectangle with the given width and height. */
  def rectangle(width: Double, height: Double): Drawing[Unit]

  /** A square with the given side length. */
  def square(width: Double): Drawing[Unit]

  /** An isoceles triangle with the given width and height. */
  def triangle(width: Double, height: Double): Drawing[Unit]

  /** A circle with the given diameter. We use diamter rather than radius so
    * circle(100) has the same size as square(100)
    */
  def circle(diameter: Double): Drawing[Unit]

  /** The empty shape, which is no shape at all. */
  def empty: Drawing[Unit]
}

/** Constructors for Shape algebra
  */
trait ShapeConstructor {
  self: BaseConstructor { type Algebra <: Shape } =>

  def link(bits: Picture[Unit], href: String): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra2: Algebra): algebra2.Drawing[Unit] = {
        algebra2.link(bits(algebra2), href)
      }
    }

  /** A rectangle with the given width and height. */
  def rectangle(width: Double, height: Double): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
        algebra.rectangle(width, height)
    }

  /** A square with the given side length. */
  def square(width: Double): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
        algebra.square(width)
    }

  /** An isoceles triangle with the given width and height. */
  def triangle(width: Double, height: Double): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
        algebra.triangle(width, height)
    }

  /** A circle with the given diameter. We use diamter rather than radius so
    * circle(100) has the same size as square(100)
    */
  def circle(diameter: Double): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
        algebra.circle(diameter)
    }

  /** The empty shape, which is no shape at all. */
  def empty: Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
        algebra.empty
    }

}
