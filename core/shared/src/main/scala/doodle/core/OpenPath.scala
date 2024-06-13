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

/** Elements are stored in reversed order to make appending, the most common
  * operation, more efficient.
  */
final case class OpenPath private (reversed: List[PathElement]) {
  def add(element: PathElement): OpenPath =
    new OpenPath(element :: reversed)

  def append(elements: List[PathElement]): OpenPath =
    new OpenPath(elements.reverse ++ reversed)
  def append(path: OpenPath): OpenPath =
    new OpenPath(path.reversed ++ reversed)

  def elements: List[PathElement] = reversed.reverse

  def close: ClosedPath = ClosedPath(reversed)

  def moveTo(point: Point): OpenPath =
    add(PathElement.moveTo(point))

  def moveTo(x: Double, y: Double): OpenPath =
    moveTo(Point.cartesian(x, y))

  def moveTo(r: Double, angle: Angle): OpenPath =
    moveTo(Point.polar(r, angle))

  def lineTo(point: Point): OpenPath =
    add(PathElement.lineTo(point))

  def lineTo(x: Double, y: Double): OpenPath =
    lineTo(Point.cartesian(x, y))

  def lineTo(r: Double, angle: Angle): OpenPath =
    lineTo(Point.polar(r, angle))

  def curveTo(cp1: Point, cp2: Point, to: Point): OpenPath =
    add(PathElement.curveTo(cp1, cp2, to))

  def curveTo(
      cp1X: Double,
      cp1Y: Double,
      cp2X: Double,
      cp2Y: Double,
      toX: Double,
      toY: Double
  ): OpenPath =
    curveTo(
      Point(cp1X, cp1Y),
      Point(cp2X, cp2Y),
      Point(toX, toY)
    )

  def curveTo(
      cp1R: Double,
      cp1Angle: Angle,
      cp2R: Double,
      cp2Angle: Angle,
      toR: Double,
      toAngle: Angle
  ): OpenPath =
    curveTo(
      Point(cp1R, cp1Angle),
      Point(cp2R, cp2Angle),
      Point(toR, toAngle)
    )
}
object OpenPath {
  val empty: OpenPath = OpenPath(List.empty)

  def apply(elts: List[PathElement]): OpenPath =
    new OpenPath(elts.reverse)

  def arc(center: Point, diameter: Double, angle: Angle): OpenPath =
    arc(center.x, center.y, diameter, angle)

  def arc(x: Double, y: Double, diameter: Double, angle: Angle): OpenPath =
    OpenPath(PathElement.arc(x, y, diameter, angle))

  def circle(center: Point, diameter: Double): OpenPath =
    circle(center.x, center.y, diameter)

  def circle(x: Double, y: Double, diameter: Double): OpenPath =
    OpenPath(PathElement.circle(x, y, diameter))

  def line(x: Double, y: Double): OpenPath =
    OpenPath(PathElement.line(x, y))

  def regularPolygon(sides: Int, radius: Double): OpenPath =
    OpenPath(PathElement.regularPolygon(sides, radius))

  def star(
      points: Int,
      outerRadius: Double,
      innerRadius: Double
  ): OpenPath =
    OpenPath(PathElement.star(points, outerRadius, innerRadius))

  def equilateralTriangle(width: Double): OpenPath =
    OpenPath(PathElement.equilateralTriangle(width))

  def triangle(width: Double, height: Double): OpenPath =
    OpenPath(PathElement.triangle(width, height))

  def rightArrow(width: Double, height: Double): OpenPath =
    OpenPath(PathElement.rightArrow(width, height))

  def roundedRectangle(
      width: Double,
      height: Double,
      radius: Double
  ): OpenPath =
    OpenPath(PathElement.roundedRectangle(width, height, radius))

  def interpolatingSpline(points: Seq[Point]): OpenPath =
    OpenPath(PathElement.interpolatingSpline(points))

  def catmulRom(
      points: Seq[Point],
      tension: Double = 0.5
  ): OpenPath =
    OpenPath(PathElement.catmulRom(points, tension))
}
