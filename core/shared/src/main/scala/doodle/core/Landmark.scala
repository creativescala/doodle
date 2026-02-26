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

/** A Landmark represents a point relative to the origin of a bounding box.
  * Landmarks are expressed in terms of two [[package.Coordinate]] values, which
  * in turn allow points to be specified using Doodle's abstract units (which
  * usually correspond to pixels) or as percentages relative to the relevant
  * [[package.BoundingBox]] edge. See the documentation for
  * [[package.Coordinate]] for more.
  */
final case class Landmark(x: Coordinate, y: Coordinate)
object Landmark {

  /** Create a Landmark from two numbers that are interpreted as percentage
    * [[package.Coordinate]].
    */
  def percent(x: Double, y: Double): Landmark =
    Landmark(Coordinate.percent(x), Coordinate.percent(y))

  /** Create a Landmark from two numbers that are interpreted as point
    * [[package.Coordinate]].
    */
  def point(x: Double, y: Double): Landmark =
    Landmark(Coordinate.point(x), Coordinate.point(y))

    /** The Landmark that refers to the origin. */
  val origin = Landmark(Coordinate.zero, Coordinate.zero)

  /** The Landmark that refers to the top left corner of the
    * [[package.BoundingBox]].
    */
  val topLeft =
    Landmark(Coordinate.minusOneHundredPercent, Coordinate.oneHundredPercent)

  /** The Landmark that refers to the top right corner of the
    * [[package.BoundingBox]].
    */
  val topRight =
    Landmark(Coordinate.oneHundredPercent, Coordinate.oneHundredPercent)

  /** The Landmark that refers to the bottom left corner of the
    * [[package.BoundingBox]].
    */
  val bottomLeft =
    Landmark(
      Coordinate.minusOneHundredPercent,
      Coordinate.minusOneHundredPercent
    )

  /** The Landmark that refers to the bottom right corner of the
    * [[package.BoundingBox]].
    */
  val bottomRight =
    Landmark(
      Coordinate.oneHundredPercent,
      Coordinate.minusOneHundredPercent
    )

  /** The Landmark that refers to the top and middle of the
    * [[package.BoundingBox]].
    */
  val topCenter =
    Landmark(Coordinate.zero, Coordinate.oneHundredPercent)

  /** The Landmark that refers to the bottom and middle of the
    * [[package.BoundingBox]].
    */
  val bottomCenter =
    Landmark(Coordinate.zero, Coordinate.minusOneHundredPercent)

  /** The Landmark that refers to the middle and left of the
    * [[package.BoundingBox]].
    */
  val centerLeft =
    Landmark(Coordinate.minusOneHundredPercent, Coordinate.zero)

  /** The Landmark that refers to the middle and right of the
    * [[package.BoundingBox]].
    */
  val centerRight =
    Landmark(Coordinate.oneHundredPercent, Coordinate.zero)
}
