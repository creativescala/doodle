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

import cats.Semigroup
import doodle.core.Angle
import doodle.core.Landmark
import doodle.core.Point
import doodle.core.Vec

trait Layout extends Algebra {

  import Layout.Scalar

  /** Place the origin of top on the origin of bottom */
  def on[A](top: Drawing[A], bottom: Drawing[A])(implicit
      s: Semigroup[A]
  ): Drawing[A]
  def beside[A](left: Drawing[A], right: Drawing[A])(implicit
      s: Semigroup[A]
  ): Drawing[A]
  def above[A](top: Drawing[A], bottom: Drawing[A])(implicit
      s: Semigroup[A]
  ): Drawing[A]

  /** Displace img by the given landmark relative to the origin, expanding the
    * bounding box if necessary to include the relocated image.
    */
  def at[A](img: Drawing[A], landmark: Landmark): Drawing[A]

  /** Place the origin of img at the given landmark, expanding the bounding box
    * if necessary to include the relocated origin.
    */
  def originAt[A](img: Drawing[A], landmark: Landmark): Drawing[A]

  /** Expand the bounding box of img by the given amounts. */
  def margin[A](
      img: Drawing[A],
      top: Double,
      right: Double,
      bottom: Double,
      left: Double
  ): Drawing[A]

  /** Expand the bounding box of img by the given amounts, evaluated relative
    * to the image's current bounding box.
    *
    * `top` and `bottom` are evaluated relative to the current bounding box
    * height; `left` and `right` are evaluated relative to the current bounding
    * box width.
    */
  def margin[A](
      img: Drawing[A],
      top: Scalar,
      right: Scalar,
      bottom: Scalar,
      left: Scalar
  ): Drawing[A]

  /** Set the width and height of the given `Drawing's` bounding box to the
    * given values. The new bounding box has the same origin as the original
    * bounding box, and extends symmetrically above and below, and left and
    * right of the origin.
    */
  def size[A](img: Drawing[A], width: Double, height: Double): Drawing[A]

  /** Set the width and height of the given `Drawing's` bounding box to values
    * evaluated relative to the current bounding box.
    *
    * `width` is evaluated relative to the current bounding box width, and
    * `height` is evaluated relative to the current bounding box height.
    */
  def size[A](img: Drawing[A], width: Scalar, height: Scalar): Drawing[A]

  // Derived methods

  def under[A](bottom: Drawing[A], top: Drawing[A])(implicit
      s: Semigroup[A]
  ): Drawing[A] =
    on(top, bottom)
  def below[A](bottom: Drawing[A], top: Drawing[A])(implicit
      s: Semigroup[A]
  ): Drawing[A] =
    above(top, bottom)

  def at[A](img: Drawing[A], x: Double, y: Double): Drawing[A] =
    at(img, Landmark.point(x, y))
  def at[A](img: Drawing[A], r: Double, a: Angle): Drawing[A] = {
    val offset = Point(r, a)
    at(img, offset.x, offset.y)
  }
  def at[A](img: Drawing[A], offset: Vec): Drawing[A] =
    at(img, offset.x, offset.y)
  def at[A](img: Drawing[A], offset: Point): Drawing[A] =
    at(img, offset.x, offset.y)

  def originAt[A](img: Drawing[A], x: Double, y: Double): Drawing[A] =
    originAt(img, Landmark.point(x, y))
  def originAt[A](img: Drawing[A], r: Double, a: Angle): Drawing[A] = {
    val offset = Point(r, a)
    originAt(img, offset.x, offset.y)
  }
  def originAt[A](img: Drawing[A], offset: Vec): Drawing[A] =
    originAt(img, offset.x, offset.y)
  def originAt[A](img: Drawing[A], offset: Point): Drawing[A] =
    originAt(img, offset.x, offset.y)

  def margin[A](img: Drawing[A], width: Double, height: Double): Drawing[A] =
    margin(img, height, width, height, width)
  def margin[A](img: Drawing[A], width: Double): Drawing[A] =
    margin(img, width, width, width, width)

  /** Utility to set the width and height to the same value. */
  def size[A](img: Drawing[A], extent: Double): Drawing[A] =
    size(img, extent, extent)
}

object Layout {

  /** A scalar magnitude that can be evaluated relative to a baseline.
    *
    * This is used for layout operations like `margin` and `size`, where we want
    * to express absolute values (usually pixels) or values relative to the
    * current bounding box (e.g. a fraction of width or height).
    */
  sealed trait Scalar {
    def eval(baseline: Double): Double
  }

  object Scalar {
    final case class Absolute(value: Double) extends Scalar {
      def eval(baseline: Double): Double = value
    }

    /** A fraction of the baseline. For example, `0.1` means 10% of the baseline. */
    final case class Fraction(value: Double) extends Scalar {
      def eval(baseline: Double): Double = baseline * value
    }

    def absolute(value: Double): Scalar = Absolute(value)
    def fraction(value: Double): Scalar = Fraction(value)
  }
}
