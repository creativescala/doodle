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

sealed abstract class PathElement extends Product with Serializable {
  // import PathElement._

  // def transform(tx: doodle.core.transform.Transform): PathElement =
  //   this match {
  //     case MoveTo(to) => MoveTo(tx(to))
  //     case LineTo(to) => LineTo(tx(to))
  //     case BezierCurveTo(cp1, cp2, to) => BezierCurveTo(tx(cp1), tx(cp2), tx(to))
  //   }
}
object PathElement {
  final case class MoveTo(to: Point) extends PathElement
  final case class LineTo(to: Point) extends PathElement
  final case class BezierCurveTo(cp1: Point, cp2: Point, to: Point) extends PathElement

  def moveTo(point: Point): PathElement =
    MoveTo(point)

  def moveTo(x: Double, y: Double): PathElement =
    moveTo(Point.cartesian(x,y))

  def moveTo(r: Double, angle: Angle): PathElement =
    moveTo(Point.polar(r,angle))


  def lineTo(point: Point): PathElement =
    LineTo(point)

  def lineTo(x: Double, y: Double): PathElement =
    lineTo(Point.cartesian(x,y))

  def lineTo(r: Double, angle: Angle): PathElement =
    lineTo(Point.polar(r,angle))

  def curveTo(cp1: Point, cp2: Point, to: Point): PathElement =
    BezierCurveTo(cp1, cp2, to)

  def curveTo(cp1X: Double, cp1Y: Double, cp2X: Double, cp2Y: Double, toX: Double, toY: Double): PathElement =
    curveTo(
      Point(cp1X, cp1Y),
      Point(cp2X, cp2Y),
      Point(toX,  toY)
    )

  def curveTo(cp1R: Double, cp1Angle: Angle, cp2R: Double, cp2Angle: Angle, toR: Double, toAngle: Angle): PathElement =
    curveTo(
      Point(cp1R, cp1Angle),
      Point(cp2R, cp2Angle),
      Point(toR,  toAngle)
    )


  /** Utility to construct a `List[PathElement]` that represents a circle. */
  def circle(center: Point, radius: Double): List[PathElement] =
    circle(center.x, center.y, radius)

  /** Utility to construct a `List[PathElement]` that represents a circle. */
  def circle(x: Double, y: Double, radius: Double): List[PathElement] = {
    import Point.cartesian
    // See http://spencermortensen.com/articles/bezier-circle/ for approximation
    // of a circle with a Bezier curve.
    val r = radius
    val c = 0.551915024494
    val cR = c * r
    List(
      MoveTo(cartesian(x, y + radius)),
      BezierCurveTo(cartesian(x + cR,  y + r),   cartesian(x + r,   y + cR),  cartesian(x + r,  y)),
      BezierCurveTo(cartesian(x + r,   y + -cR), cartesian(x + cR,  y + -r),  cartesian(x,      y + -r)),
      BezierCurveTo(cartesian(x + -cR, y + -r),  cartesian(x + -r,  y + -cR), cartesian(x + -r, y)),
      BezierCurveTo(cartesian(x + -r,  y + cR),  cartesian(x + -cR, y + r),   cartesian(x,      y + r))
    )
  }
}
