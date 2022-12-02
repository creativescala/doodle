/*
 * Copyright 2015 Noel Welsh
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

/** Representation of an affine transformation as an augmented matrix. */
final case class Transform(elements: Array[Double]) {
  def apply(point: Point): Point = {
    val x = point.x
    val y = point.y

    val newX = (x * elements(0)) + (y * elements(1)) + elements(2)
    val newY = (x * elements(3)) + (y * elements(4)) + elements(5)

    Point(newX, newY)
  }

  def apply(vec: Vec): Vec =
    this.apply(vec.toPoint).toVec

  def andThen(that: Transform): Transform = {
    val a = this.elements
    val b = that.elements
    Transform(
      Array(
        b(0) * a(0) + b(1) * a(3),
        b(0) * a(1) + b(1) * a(4),
        b(0) * a(2) + b(1) * a(5) + b(2),
        b(3) * a(0) + b(4) * a(3),
        b(3) * a(1) + b(4) * a(4),
        b(3) * a(2) + b(4) * a(5) + b(5),
        0,
        0,
        1
      )
    )
  }

  def scale(x: Double, y: Double): Transform =
    this.andThen(Transform.scale(x, y))

  def rotate(angle: Angle): Transform =
    this.andThen(Transform.rotate(angle))

  def translate(x: Double, y: Double): Transform =
    this.andThen(Transform.translate(x, y))

  def translate(v: Vec): Transform =
    this.andThen(Transform.translate(v))

  override def toString(): String = {
    val elts = this.elements
    s"""Transform([${elts(0)}, ${elts(1)}, ${elts(2)},
                   ${elts(3)}, ${elts(4)}, ${elts(5)},
                   ${elts(6)}, ${elts(7)}, ${elts(8)}}])"""
  }

  override def equals(that: Any): Boolean = {
    that.isInstanceOf[Transform] && {
      val other = that.asInstanceOf[Transform]
      var i = 0
      var isEqual = true
      while (i < elements.length) {
        if (this.elements(i) != other.elements(i))
          isEqual = false

        i = i + 1
      }
      isEqual
    }
  }

}
object Transform {
  val identity = scale(1.0, 1.0)

  def scale(x: Double, y: Double): Transform =
    Transform(Array(x, 0, 0, 0, y, 0, 0, 0, 1))

  def rotate(angle: Angle): Transform =
    Transform(Array(angle.cos, -angle.sin, 0, angle.sin, angle.cos, 0, 0, 0, 1))

  def translate(x: Double, y: Double): Transform =
    Transform(Array(1, 0, x, 0, 1, y, 0, 0, 1))

  def translate(v: Vec): Transform =
    translate(v.x, v.y)

  /** Reflect horizontally (around the Y-axis) */
  val horizontalReflection: Transform =
    Transform(Array(-1, 0, 0, 0, 1, 0, 0, 0, 1))

  /** Reflect vertically (around the X-axis) */
  val verticalReflection: Transform =
    Transform(Array(1, 0, 0, 0, -1, 0, 0, 0, 1))

  /** Convert from the usual cartesian coordinate system (origin in the center,
    * x and y increase towards the top right) to usual screen coordinate system
    * (origin in the top left, x and y increase to the bottom right).
    */
  def logicalToScreen(width: Double, height: Double): Transform =
    // A composition of a reflection and a translation
    Transform(Array(1, 0, width / 2.0, 0, -1, height / 2.0, 0, 0, 1))

  /** Convert from the usual screen coordinate system (origin in the top left, x
    * and y increase to the bottom right) to the usual cartesian coordinate
    * system (origin in the center, x and y increase towards the top right).
    */
  def screenToLogical(width: Double, height: Double): Transform =
    // A composition of a reflection and a translation
    Transform(Array(1, 0, -width / 2.0, 0, -1, height / 2.0, 0, 0, 1))
}
