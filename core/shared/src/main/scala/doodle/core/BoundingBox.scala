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
package core

/** A [[doodle.core.BoundingBox]] represents a bounding box around an picture.
  *
  * A bounding box also defines a local coordinate system for a picture. The
  * bounding box must contain the origin of the coordinate system. However the
  * origin need not be centered within the box.
  *
  * No particular guarantees are made about the tightness of the bounding box,
  * though it can assumed to be reasonably tight.
  */
final case class BoundingBox(
    left: Double,
    top: Double,
    right: Double,
    bottom: Double
) {
  def width: Double = right - left
  def height: Double = top - bottom

  def on(that: BoundingBox): BoundingBox =
    BoundingBox(
      this.left min that.left,
      this.top max that.top,
      this.right max that.right,
      this.bottom min that.bottom
    )

  def beside(that: BoundingBox): BoundingBox =
    BoundingBox(
      -(this.width + that.width) / 2.0,
      this.top max that.top,
      (this.width + that.width) / 2.0,
      this.bottom min that.bottom
    )

  def above(that: BoundingBox): BoundingBox =
    BoundingBox(
      this.left min that.left,
      (this.height + that.height) / 2.0,
      this.right max that.right,
      -(this.height + that.height) / 2.0
    )

  /** Evaluate the landmark relative to the origin of this bounding box,
    * returning the location described by the landmark.
    */
  def eval(landmark: Landmark): Point = {
    Point(landmark.x.eval(left, right), landmark.y.eval(bottom, top))
  }

  def at(point: Point): BoundingBox = {
    val x = point.x
    val y = point.y

    val newLeft = (left + x) min 0
    val newTop = (top + y) max 0
    val newRight = (right + x) max 0
    val newBottom = (bottom + y) min 0

    BoundingBox(newLeft, newTop, newRight, newBottom)
  }

  def at(landmark: Landmark): BoundingBox =
    at(eval(landmark))

  def originAt(landmark: Landmark): BoundingBox =
    originAt(eval(landmark))

  def originAt(point: Point): BoundingBox = {
    // Vector maths to work out where the edges of the bounding box lie in
    // relation to the new origin.
    val newTopLeft = Point(left, top) - point
    val newBottomRight = Point(right, bottom) - point

    // Make sure the bounding box includes the origin
    val newLeft = newTopLeft.x min 0
    val newTop = newTopLeft.y max 0
    val newRight = newBottomRight.x max 0
    val newBottom = newBottomRight.y min 0

    BoundingBox(newLeft, newTop, newRight, newBottom)
  }

  /** Expand bounding box to enclose the given `Point`. */
  def enclose(toInclude: Point): BoundingBox =
    BoundingBox(
      left min toInclude.x,
      top max toInclude.y,
      right max toInclude.x,
      bottom min toInclude.y
    )

  /** Add `expansion` to all sides of this bounding box. */
  def expand(expansion: Double): BoundingBox =
    BoundingBox(
      this.left - expansion,
      this.top + expansion,
      this.right + expansion,
      this.bottom - expansion
    )

  def transform(tx: Transform): BoundingBox = {
    BoundingBox.empty
      .enclose(tx(Point(left, top)))
      .enclose(tx(Point(right, top)))
      .enclose(tx(Point(left, bottom)))
      .enclose(tx(Point(right, bottom)))
  }
}

object BoundingBox {
  val empty = BoundingBox(0, 0, 0, 0)

  /** Create a [[doodle.core.BoundingBox]] with the given width and height and
    * the origin centered within the box.
    */
  def centered(width: Double, height: Double): BoundingBox = {
    val w = width / 2.0
    val h = height / 2.0

    BoundingBox(-w, h, w, -h)
  }
}
