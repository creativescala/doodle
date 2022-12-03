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

/** A Landmark represents a position relative to the origin of a bounding box.
  */
final case class Landmark(x: Coordinate, y: Coordinate)
object Landmark {

  def percent(x: Double, y: Double): Landmark =
    Landmark(Coordinate.percent(x), Coordinate.percent(y))

  def point(x: Double, y: Double): Landmark =
    Landmark(Coordinate.point(x), Coordinate.point(y))

  val origin = Landmark(Coordinate.zero, Coordinate.zero)
  val topLeft =
    Landmark(Coordinate.minusOneHundredPercent, Coordinate.oneHundredPercent)
  val topRight =
    Landmark(Coordinate.oneHundredPercent, Coordinate.oneHundredPercent)
  val bottomLeft =
    Landmark(
      Coordinate.minusOneHundredPercent,
      Coordinate.minusOneHundredPercent
    )
  val bottomRight =
    Landmark(
      Coordinate.oneHundredPercent,
      Coordinate.minusOneHundredPercent
    )
}
