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
  final case class BezierCurveTo(cp1: Point, cp2: Point, to: Point)
      extends PathElement

  def moveTo(point: Point): PathElement =
    MoveTo(point)

  def moveTo(x: Double, y: Double): PathElement =
    moveTo(Point.cartesian(x, y))

  def moveTo(r: Double, angle: Angle): PathElement =
    moveTo(Point.polar(r, angle))

  def lineTo(point: Point): PathElement =
    LineTo(point)

  def lineTo(x: Double, y: Double): PathElement =
    lineTo(Point.cartesian(x, y))

  def lineTo(r: Double, angle: Angle): PathElement =
    lineTo(Point.polar(r, angle))

  def curveTo(cp1: Point, cp2: Point, to: Point): PathElement =
    BezierCurveTo(cp1, cp2, to)

  def curveTo(
      cp1X: Double,
      cp1Y: Double,
      cp2X: Double,
      cp2Y: Double,
      toX: Double,
      toY: Double
  ): PathElement =
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
  ): PathElement =
    curveTo(
      Point(cp1R, cp1Angle),
      Point(cp2R, cp2Angle),
      Point(toR, toAngle)
    )

  /** Utility to construct a `List[PathElement]` that represents a circle. */
  def circle(center: Point, diameter: Double): List[PathElement] =
    circle(center.x, center.y, diameter)

  /** Utility to construct a `List[PathElement]` that represents a circle. */
  def circle(x: Double, y: Double, diameter: Double): List[PathElement] = {
    import Point.cartesian
    // See http://spencermortensen.com/articles/bezier-circle/ for approximation
    // of a circle with a Bezier curve.
    val r = diameter / 2.0
    val c = 0.551915024494
    val cR = c * r
    List(
      MoveTo(cartesian(x, y + r)),
      BezierCurveTo(
        cartesian(x + cR, y + r),
        cartesian(x + r, y + cR),
        cartesian(x + r, y)
      ),
      BezierCurveTo(
        cartesian(x + r, y + -cR),
        cartesian(x + cR, y + -r),
        cartesian(x, y + -r)
      ),
      BezierCurveTo(
        cartesian(x + -cR, y + -r),
        cartesian(x + -r, y + -cR),
        cartesian(x + -r, y)
      ),
      BezierCurveTo(
        cartesian(x + -r, y + cR),
        cartesian(x + -cR, y + r),
        cartesian(x, y + r)
      )
    )
  }

  /** Utility to construct a `List[PathElement]` that represents a circular arc.
    * The arc starts at the 3-o'clock position and rotates counter-clockwise to
    * the given `angle`.
    *
    * The `angle` must be greater than zero.
    */
  def arc(
      x: Double,
      y: Double,
      diameter: Double,
      angle: Angle
  ): List[PathElement] = {
    val r = diameter / 2.0
    val c = 0.551915024494
    val cR = c * r

    // Create a bezier curve describing an arc from start to end angle
    // Arc should be less than 90 degrees to have acceptable error
    // Formula for arc from https://ecridge.com/bezier.pdf
    def smallArc(startAngle: Angle, endAngle: Angle): PathElement = {
      val origin = Point(x, y)
      val angle = endAngle - startAngle
      val alpha = angle / 2.0
      val lambda = (4.0 - alpha.cos) / 3.0
      val mu =
        alpha.sin + (((4.0 * (alpha.cos - 1)) / 3.0) * (alpha.cos / alpha.sin))

      // The points that define the bezier relative to the origin. We don't need
      // the start (a) so it's commented out but left in the the code to make it
      // clearer.
      //
      // val a = Vec(r, -alpha).rotate(startAngle + angle)
      val b = (Vec(lambda, -mu) * r).rotate(startAngle + alpha)
      val c = (Vec(lambda, mu) * r).rotate(startAngle + alpha)
      val d = Vec(r, alpha).rotate(startAngle + alpha)

      // Displace relative to x and y
      BezierCurveTo(origin + b, origin + c, origin + d)
    }

    if angle > Angle.one then circle(Point.zero, diameter)
    else if angle > Angle.threeQuarters then {
      List(
        moveTo(x + r, y),
        BezierCurveTo(
          Point(x + r, y + cR),
          Point(x + cR, y + r),
          Point(x, y + r)
        ),
        BezierCurveTo(
          Point(x + -cR, y + r),
          Point(x + -r, y + cR),
          Point(x + -r, y)
        ),
        BezierCurveTo(
          Point(x + -r, y + -cR),
          Point(x + -cR, y + -r),
          Point(x, y + -r)
        ),
        smallArc(Angle.threeQuarters, angle)
      )
    } else if angle > Angle.oneHalf then {
      List(
        moveTo(x + r, y),
        BezierCurveTo(
          Point(x + r, y + cR),
          Point(x + cR, y + r),
          Point(x, y + r)
        ),
        BezierCurveTo(
          Point(x + -cR, y + r),
          Point(x + -r, y + cR),
          Point(x + -r, y)
        ),
        smallArc(Angle.oneHalf, angle)
      )
    } else if angle > Angle.oneQuarter then {
      List(
        moveTo(x + r, y),
        BezierCurveTo(
          Point(x + r, y + cR),
          Point(x + cR, y + r),
          Point(x, y + r)
        ),
        smallArc(Angle.oneQuarter, angle)
      )
    } else if angle > Angle.zero then
      List(moveTo(x + r, y), smallArc(Angle.zero, angle))
    else List()
  }

  /** Construct a regular polygon
    */
  def regularPolygon(
      sides: Int,
      radius: Double
  ): List[PathElement] = {
    val rotation = Angle.one / sides.toDouble
    val path =
      (1 to sides).map { n =>
        lineTo(radius, rotation * n.toDouble)
      }.toList

    (moveTo(radius, Angle.zero) +: path)
  }

  /** Construct a star
    */
  def star(
      points: Int,
      outerRadius: Double,
      innerRadius: Double
  ): List[PathElement] = {

    val rotation = Angle.one / (points * 2.0)
    val path =
      (1 to (points * 2)).map { n =>
        if n % 2 == 0 then lineTo(outerRadius, rotation * n.toDouble)
        else lineTo(innerRadius, rotation * n.toDouble)
      }.toList

    (moveTo(outerRadius, Angle.zero) +: path)
  }

  val oneOnSqrt3 = 1.0 / math.sqrt(3.0)

  /** Construct a line with the origin at the center of the line */
  def line(x: Double, y: Double): List[PathElement] = {
    val startX = -x / 2
    val startY = -y / 2
    val endX = x / 2
    val endY = y / 2

    List(
      PathElement.moveTo(startX, startY),
      PathElement.lineTo(endX, endY)
    )
  }

  /** Construct an equilateral triangle */
  def equilateralTriangle(width: Double): List[PathElement] = {
    List(
      moveTo(Point.zero),
      moveTo(0, width * oneOnSqrt3),
      lineTo(width / 2.0, -width * oneOnSqrt3 * 0.5),
      lineTo(-width / 2.0, -width * oneOnSqrt3 * 0.5),
      lineTo(0, width * oneOnSqrt3)
    )
  }

  /** Construct an isoceles triangle with the given height and widht */
  def triangle(width: Double, height: Double): List[PathElement] = {
    val w = width / 2.0
    val h = height / 2.0

    List(moveTo(-w, -h), lineTo(0, h), lineTo(w, -h))
  }

  /** Construct an arrow pointing to the right */
  def rightArrow(width: Double, height: Double): List[PathElement] = {
    val path = List(
      moveTo(width / 2, 0),
      lineTo(0, height / 2),
      lineTo(0, height * 0.2),
      lineTo(-width / 2, height * 0.2),
      lineTo(-width / 2, -height * 0.2),
      lineTo(0, -height * 0.2),
      lineTo(0, -height / 2),
      lineTo(width / 2, 0)
    )

    path
  }

  /** Construct a rounded rectangle with the given width, height, and corner
    * radius
    */
  def roundedRectangle(
      width: Double,
      height: Double,
      radius: Double
  ): List[PathElement] = {
    // Clamp radius to the smallest of width and height
    val cornerRadius =
      if radius > width / 2 || radius > height / 2 then
        (width / 2) min (height / 2)
      else radius

    // Magic number for drawing circles with bezier curves
    // See http://spencermortensen.com/articles/bezier-circle/ for approximation
    // of a circle with a Bezier curve.
    val c = (4.0 / 3.0) * (Math.sqrt(2) - 1)
    val cR = c * cornerRadius

    val elts = List(
      moveTo(width / 2 - cornerRadius, height / 2),
      curveTo(
        width / 2 - cornerRadius + cR,
        height / 2,
        width / 2,
        height / 2 - cornerRadius + cR,
        width / 2,
        height / 2 - cornerRadius
      ),
      lineTo(width / 2, -height / 2 + cornerRadius),
      curveTo(
        width / 2,
        -height / 2 + cornerRadius - cR,
        width / 2 - cornerRadius + cR,
        -height / 2,
        width / 2 - cornerRadius,
        -height / 2
      ),
      lineTo(-width / 2 + cornerRadius, -height / 2),
      curveTo(
        -width / 2 + cornerRadius - cR,
        -height / 2,
        -width / 2,
        -height / 2 + cornerRadius - cR,
        -width / 2,
        -height / 2 + cornerRadius
      ),
      lineTo(-width / 2, height / 2 - cornerRadius),
      curveTo(
        -width / 2,
        height / 2 - cornerRadius + cR,
        -width / 2 + cornerRadius - cR,
        height / 2,
        -width / 2 + cornerRadius,
        height / 2
      ),
      lineTo(width / 2 - cornerRadius, height / 2)
    )

    elts
  }

  /** Construct list of bezier curves that are smoothly connected and intersect
    * all the given points. Defaults to `catmulRom` with the default tension.
    */
  def interpolatingSpline(points: Seq[Point]): List[PathElement] =
    catmulRom(points)

  /** Interpolate a spline (a curve) that passes through all the given points,
    * using the Catmul Rom formulation (see, e.g.,
    * https://en.wikipedia.org/wiki/Cubic_Hermite_spline)
    *
    * The tension can be changed to control how tightly the curve turns. It
    * defaults to 0.5.
    *
    * The Catmul Rom algorithm requires a point before and after each pair of
    * points that define the curve. To meet this condition for the first and
    * last points in `points`, they are repeated.
    *
    * If `points` has less than two elements an empty List is returned.
    */
  def catmulRom(
      points: Seq[Point],
      tension: Double = 0.5
  ): List[PathElement] = {
    /*
    To convert Catmul Rom curve to a Bezier curve, multiply points by (invB * catmul)

    See, for example, http://www.almightybuserror.com/2009/12/04/drawing-splines-in-opengl.html

    Inverse Bezier matrix
    val invB = Array[Double](0, 0,       0,       1,
                             0, 0,       1.0/3.0, 1,
                             0, 1.0/3.0, 2.0/3.0, 1,
                             1, 1,       1,       1)

    Catmul matrix with given tension
    val catmul = Array[Double](-tension,    2 - tension,  tension - 2,       tension,
                               2 * tension, tension - 3,  3 - (2 * tension), -tension,
                               -tension,    0,            tension,           0,
                               0,           1,            0,                 0)

    invB * catmul
    val matrix = Array[Double](0,            1,           0,           0,
                               -tension/3.0, 1,           tension/3.0, 0,
                               0,            tension/3.0, 1,           -tension/3.0,
                               0,            0,           1,           0)
     */
    def toCurve(pt0: Point, pt1: Point, pt2: Point, pt3: Point): PathElement =
      PathElement.curveTo(
        ((-tension * pt0.x) + 3 * pt1.x + (tension * pt2.x)) / 3.0,
        ((-tension * pt0.y) + 3 * pt1.y + (tension * pt2.y)) / 3.0,
        ((tension * pt1.x) + 3 * pt2.x - (tension * pt3.x)) / 3.0,
        ((tension * pt1.y) + 3 * pt2.y - (tension * pt3.y)) / 3.0,
        pt2.x,
        pt2.y
      )

    def iter(points: List[Point]): List[PathElement] = {
      points match {
        case pt0 :: pt1 :: pt2 :: pt3 :: pts =>
          toCurve(pt0, pt1, pt2, pt3) +: iter(pt1 +: pt2 +: pt3 +: pts)

        case pt0 :: pt1 :: pt2 :: Seq() =>
          // Case where we've reached the end of the sequence of points
          // We repeat the last point
          val pt3 = pt2
          List(toCurve(pt0, pt1, pt2, pt3))

        case _ =>
          // There were two or fewer points in the sequence
          List.empty[PathElement]
      }
    }

    points.headOption.fold(List.empty[PathElement]) { pt0 =>
      (PathElement.moveTo(pt0) :: iter(pt0 :: points.toList))
    }
  }

}
