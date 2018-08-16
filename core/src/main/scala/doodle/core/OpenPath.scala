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
package core

/** Elements are stored in reversed order to make appending, the most common operation, more efficient. */
final case class OpenPath(reversed: List[PathElement]) {
  def add(element: PathElement): OpenPath =
    OpenPath(element :: reversed)

  def append(elements: List[PathElement]): OpenPath =
    OpenPath(elements.reverse ++ reversed)
  def append(path: OpenPath): OpenPath =
    OpenPath(path.reversed ++ reversed)

  def elements: List[PathElement] = reversed.reverse

  def close: ClosedPath = ClosedPath(reversed)


  def moveTo(point: Point): OpenPath =
    add(PathElement.moveTo(point))

  def moveTo(x: Double, y: Double): OpenPath =
    moveTo(Point.cartesian(x,y))

  def moveTo(r: Double, angle: Angle): OpenPath =
    moveTo(Point.polar(r,angle))


  def lineTo(point: Point): OpenPath =
    add(PathElement.lineTo(point))

  def lineTo(x: Double, y: Double): OpenPath =
    lineTo(Point.cartesian(x,y))

  def lineTo(r: Double, angle: Angle): OpenPath =
    lineTo(Point.polar(r,angle))


  def curveTo(cp1: Point, cp2: Point, to: Point): OpenPath =
    add(PathElement.curveTo(cp1, cp2, to))

  def curveTo(cp1X: Double, cp1Y: Double, cp2X: Double, cp2Y: Double, toX: Double, toY: Double): OpenPath =
    curveTo(
      Point(cp1X, cp1Y),
      Point(cp2X, cp2Y),
      Point(toX,  toY)
    )

  def curveTo(cp1R: Double, cp1Angle: Angle, cp2R: Double, cp2Angle: Angle, toR: Double, toAngle: Angle): OpenPath =
    curveTo(
      Point(cp1R, cp1Angle),
      Point(cp2R, cp2Angle),
      Point(toR,  toAngle)
    )
}
object OpenPath {
  val empty: OpenPath = OpenPath(List.empty)
}
