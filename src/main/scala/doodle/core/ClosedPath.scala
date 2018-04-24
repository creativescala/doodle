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
final case class ClosedPath(reversed: List[PathElement]) {
  def add(element: PathElement): ClosedPath =
    ClosedPath(element :: reversed)

  def append(elements: List[PathElement]): ClosedPath =
    ClosedPath(elements.reverse ++ reversed)
  def append(path: ClosedPath): ClosedPath =
    ClosedPath(path.reversed ++ reversed)

  def elements: List[PathElement] = reversed.reverse

  def open: OpenPath = OpenPath(reversed)


  def moveTo(point: Point): ClosedPath =
    add(PathElement.moveTo(point))

  def moveTo(x: Double, y: Double): ClosedPath =
    moveTo(Point.cartesian(x,y))

  def moveTo(r: Double, angle: Angle): ClosedPath =
    moveTo(Point.polar(r,angle))


  def lineTo(point: Point): ClosedPath =
    add(PathElement.lineTo(point))

  def lineTo(x: Double, y: Double): ClosedPath =
    lineTo(Point.cartesian(x,y))

  def lineTo(r: Double, angle: Angle): ClosedPath =
    lineTo(Point.polar(r,angle))


  def curveTo(cp1: Point, cp2: Point, to: Point): ClosedPath =
    add(PathElement.curveTo(cp1, cp2, to))

  def curveTo(cp1X: Double, cp1Y: Double, cp2X: Double, cp2Y: Double, toX: Double, toY: Double): ClosedPath =
    curveTo(
      Point(cp1X, cp1Y),
      Point(cp2X, cp2Y),
      Point(toX,  toY)
    )

  def curveTo(cp1R: Double, cp1Angle: Angle, cp2R: Double, cp2Angle: Angle, toR: Double, toAngle: Angle): ClosedPath =
    curveTo(
      Point(cp1R, cp1Angle),
      Point(cp2R, cp2Angle),
      Point(toR,  toAngle)
    )
}
object ClosedPath {
  val empty: ClosedPath = ClosedPath(List.empty)
}
