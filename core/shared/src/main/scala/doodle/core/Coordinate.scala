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
package core

/** A Coordinate represents a position on an axis relative to a
  * [[package.BoundingBox]] origin. Coordinates can be specified as a (1D) point
  * or as a percentage relative to the edge of the bounding box.
  *
  * For example, `Coordinate.point(10)` is ten units from the origin in the
  * positive direction, while `Coordinate.percent(100)` is the positive (top or
  * right) edge of the bounding box.
  */
enum Coordinate {

  /** Values must be normalized so 100% is represented as 1.0 */
  case Percent(value: Double)
  case Point(value: Double)
  case Add(left: Coordinate, right: Coordinate)
  case Subtract(left: Coordinate, right: Coordinate)

  /** Add together two Coordinates. */
  def add(that: Coordinate): Coordinate =
    Add(this, that)

  /** Add together two Coordinates. */
  def +(that: Coordinate): Coordinate =
    Add(this, that)

  /** Subtract two Coordinates. */
  def subtract(that: Coordinate): Coordinate =
    Subtract(this, that)

  /** Subtract two Coordinates. */
  def -(that: Coordinate): Coordinate =
    Subtract(this, that)

  /** Convert the Coordinate to a value in Doodle's abstract units (which
    * usually refer to pixels) relative to the origin, given values for -100%
    * and +100%
    */
  def eval(negative: Double, positive: Double): Double =
    this match {
      case Percent(value) =>
        if value < 0 then Math.abs(value) * negative
        else value * positive

      case Point(value) => value

      case Add(l, r) => l.eval(negative, positive) + r.eval(negative, positive)

      case Subtract(l, r) =>
        l.eval(negative, positive) - r.eval(negative, positive)
    }
}
object Coordinate {

  /** Construct a [[package.Coordinate]] as a percentage value. For example,
    * `Coordinate.percent(100)` represents 100% of the right or top edge of the
    * bounding box, while `Coordinate.percent(0)` is the origin.
    */
  def percent(value: Double): Coordinate = Percent(value / 100.0)

  /** Construct a [[package.Coordinate]] as a point value, expressed in Doodle's
    * abstract units (which usually refer to pixels). `Coordinate.point(100)`
    * represents 100 units towards the right or top edge of the bounding box,
    * while `Coordinate.percent(0)` is the origin.
    */
  def point(value: Double): Coordinate = Point(value)
  val zero = point(0.0)
  val oneHundredPercent = percent(100)
  val minusOneHundredPercent = percent(-100)
}
