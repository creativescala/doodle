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
package image

import doodle.core.*
import doodle.core.font.{Font as CoreFont}
import doodle.language.Basic

sealed abstract class Image extends Product with Serializable {
  import Image.Elements.*

  // Layout ----------------------------------------------------------

  def beside(right: Image): Image =
    Beside(this, right)

  def on(bottom: Image): Image =
    On(this, bottom)

  def under(top: Image): Image =
    On(top, this)

  def above(bottom: Image): Image =
    Above(this, bottom)

  def below(top: Image): Image =
    Above(top, this)

  def margin(
      top: Double,
      right: Double,
      bottom: Double,
      left: Double
  ): Image =
    Margin(this, top, right, bottom, left)

  def margin(width: Double, height: Double): Image =
    Margin(this, height, width, height, width)

  def margin(width: Double): Image =
    Margin(this, width, width, width, width)

  def size(width: Double, height: Double): Image =
    Size(this, width, height)

  // Context Transform ------------------------------------------------

  def strokeColor(color: Color): Image =
    StrokeColor(this, color)

  // def strokeColorTransform(f: Color => Color): Image =
  //   ContextTransform(_.strokeColorTransform(f), this)

  def strokeWidth(width: Double): Image =
    StrokeWidth(this, width)

  def strokeCap[A](strokeCap: Cap): Image =
    StrokeCap(this, strokeCap)

  def strokeJoin[A](strokeJoin: Join): Image =
    StrokeJoin(this, strokeJoin)

  /** Specify the stroke dash pattern. The pattern gives the length, in local
    * coordinates, of opaque and transparent sections. The first element is the
    * length of an opaque section, the second of a transparent section, and so
    * on.
    */
  def strokeDash[A](pattern: Iterable[Double]): Image =
    StrokeDash(this, pattern)

  def fillColor(color: Color): Image =
    FillColor(this, color)

  def fillGradient(gradient: Gradient): Image =
    FillGradient(this, gradient)

  // def fillColorTransform(f: Color => Color): Image =
  //   ContextTransform(_.fillColorTransform(f), this)

  // def fillGradient(gradient: Gradient): Image =
  //   ContextTransform(_.fillGradient(gradient), this)

  // def fillGradientTransform(f: Gradient => Gradient): Image =
  //   ContextTransform(_.fillGradientTransform(f), this)

  def noStroke: Image =
    NoStroke(this)

  def noFill: Image =
    NoFill(this)

  def font(font: CoreFont): Image =
    Font(this, font)

  // Affine Transform -------------------------------------------------

  def transform(tx: core.Transform): Image =
    Transform(tx, this)

  def rotate(angle: Angle): Image =
    this.transform(core.Transform.rotate(angle))

  def scale(x: Double, y: Double): Image =
    this.transform(core.Transform.scale(x, y))

  def at(landmark: Landmark): Image =
    At(this, landmark)

  def at(vec: Vec): Image =
    // Transform(core.transform.Transform.translate(vec.x, vec.y), this)
    at(vec.x, vec.y)

  def at(pt: Point): Image =
    at(pt.x, pt.y)

  def at(x: Double, y: Double): Image =
    // Transform(core.transform.Transform.translate(x, y), this)
    at(Landmark.point(x, y))

  def at(r: Double, a: Angle): Image = {
    val pt = Point(r, a)
    at(pt.x, pt.y)
  }

  def originAt(landmark: Landmark): Image =
    OriginAt(this, landmark)

  def originAt(vec: Vec): Image =
    originAt(vec.x, vec.y)

  def originAt(pt: Point): Image =
    originAt(pt.x, pt.y)

  def originAt(x: Double, y: Double): Image =
    originAt(Landmark.point(x, y))

  def originAt(r: Double, a: Angle): Image = {
    val pt = Point(r, a)
    originAt(pt.x, pt.y)
  }

  // Debug ------------------------------------------------------------

  def debug(color: Color): Image =
    Debug(this, color)

  def debug: Image =
    Debug(this, Color.crimson)

  // Convert to tagless final format

  def compile[Algebra <: Basic]: doodle.algebra.Picture[Algebra, Unit] =
    Image.compile(this)
}
object Image {

  /** Contains the leaves of the Image algebraic data type. Packaged here so
    * they don't pollute the namespace when importing Image to access to the
    * smart constructors.
    */
  object Elements {
    final case class OpenPath(path: doodle.core.OpenPath) extends Image
    final case class ClosedPath(path: doodle.core.ClosedPath) extends Image
    final case class Text(get: String) extends Image
    final case class Circle(d: Double) extends Image
    final case class Raster[A](w: Int, h: Int, f: A => Unit) extends Image
    final case class Rectangle(w: Double, h: Double) extends Image
    final case class Triangle(w: Double, h: Double) extends Image
    // final case class Draw(w: Double, h: Double, f: Canvas => Unit) extends Image
    final case class Beside(l: Image, r: Image) extends Image
    final case class Above(l: Image, r: Image) extends Image
    final case class On(t: Image, b: Image) extends Image
    final case class At(image: Image, landmark: Landmark) extends Image
    final case class Transform(tx: core.Transform, i: Image) extends Image
    final case class Margin(
        i: Image,
        top: Double,
        right: Double,
        bottom: Double,
        left: Double
    ) extends Image
    final case class Size(image: Image, width: Double, height: Double)
        extends Image
    final case class OriginAt(image: Image, landmark: Landmark) extends Image
    // Style
    final case class StrokeWidth(image: Image, width: Double) extends Image
    final case class StrokeColor(image: Image, color: Color) extends Image
    final case class StrokeCap(image: Image, cap: Cap) extends Image
    final case class StrokeJoin(image: Image, join: Join) extends Image
    final case class StrokeDash(image: Image, pattern: Iterable[Double])
        extends Image

    final case class FillColor(image: Image, color: Color) extends Image
    final case class FillGradient(image: Image, gradient: Gradient)
        extends Image
    final case class NoStroke(image: Image) extends Image
    final case class NoFill(image: Image) extends Image
    final case class Font(image: Image, font: CoreFont) extends Image
    // Debug
    final case class Debug(image: Image, color: Color) extends Image

    case object Empty extends Image
  }
  import Elements.*

  // Smart constructors

  def path(path: doodle.core.ClosedPath): Image =
    ClosedPath(path)

  def path(path: doodle.core.OpenPath): Image =
    OpenPath(path)

  def text(characters: String): Image =
    Text(characters)

  def line(x: Double, y: Double): Image =
    path(doodle.core.OpenPath.line(x, y))

  def arc(diameter: Double, angle: Angle): Image =
    path(doodle.core.OpenPath.arc(0.0, 0.0, diameter, angle))

  def circle(diameter: Double): Image =
    Circle(diameter)

  def pie(diameter: Double, angle: Angle): Image =
    path(doodle.core.ClosedPath.pie(0.0, 0.0, diameter, angle))

  def raster[A](width: Int, height: Int)(f: A => Unit): Image =
    Raster(width, height, f)  

  def rectangle(width: Double, height: Double): Image =
    Rectangle(width, height)

  def square(side: Double): Image =
    rectangle(side, side)

  def regularPolygon(sides: Int, radius: Double): Image =
    path(doodle.core.ClosedPath.regularPolygon(sides, radius))

  def star(points: Int, outerRadius: Double, innerRadius: Double): Image =
    path(doodle.core.ClosedPath.star(points, outerRadius, innerRadius))

  def rightArrow(width: Double, height: Double): Image =
    path(doodle.core.ClosedPath.rightArrow(width, height))

  def roundedRectangle(width: Double, height: Double, radius: Double): Image =
    path(doodle.core.ClosedPath.roundedRectangle(width, height, radius))

  def equilateralTriangle(width: Double): Image =
    path(doodle.core.ClosedPath.equilateralTriangle(width))

  def triangle(width: Double, height: Double): Image =
    Triangle(width, height)

  /** Construct an open path of bezier curves that intersects all the given
    * points. Defaults to `catmulRom` with the default tension.
    */
  def interpolatingSpline(points: Seq[Point]): Image =
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
    * If `points` has less than two elements an empty `Path` is returned.
    */
  def catmulRom(points: Seq[Point], tension: Double = 0.5): Image =
    path(doodle.core.OpenPath.catmulRom(points, tension))

  val empty: Image =
    Empty

  /** Compile an `Image` to a `doodle.algebra.Picture` */
  def compile[Alg <: Basic](
      image: Image
  ): doodle.algebra.Picture[Alg, Unit] = {
    import cats.instances.unit.*
    import Elements.*

    new doodle.algebra.Picture[Alg, Unit] {
      def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
        image match {
          case OpenPath(path) =>
            algebra.path(path)
          case ClosedPath(path) =>
            algebra.path(path)

          case Text(t) =>
            algebra.text(t)
          case Font(image, f) =>
            algebra.font(compile(image)(algebra), f)

          case Circle(d) =>
            algebra.circle(d)
          case Raster(w, h, f) =>
            algebra.raster(w, h)(f)
          case Rectangle(w, h) =>
            algebra.rectangle(w, h)
          case Triangle(w, h) =>
            algebra.triangle(w, h)

          case Beside(l, r) =>
            algebra.beside(compile(l)(algebra), compile(r)(algebra))
          case Above(l, r) =>
            algebra.above(compile(l)(algebra), compile(r)(algebra))
          case On(t, b) =>
            algebra.on(compile(t)(algebra), compile(b)(algebra))
          case At(image, landmark) =>
            algebra.at(compile(image)(algebra), landmark)
          case Margin(image, top, right, bottom, left) =>
            algebra.margin(compile(image)(algebra), top, right, bottom, left)
          case Size(image, width, height) =>
            algebra.size(compile(image)(algebra), width, height)
          case OriginAt(image, landmark) =>
            algebra.originAt(compile(image)(algebra), landmark)

          case Transform(tx, i) =>
            algebra.transform(compile(i)(algebra), tx)

          case StrokeWidth(image, width) =>
            algebra.strokeWidth(compile(image)(algebra), width)
          case StrokeColor(image, color) =>
            algebra.strokeColor(compile(image)(algebra), color)
          case StrokeCap(image, cap) =>
            algebra.strokeCap(compile(image)(algebra), cap)
          case StrokeJoin(image, join) =>
            algebra.strokeJoin(compile(image)(algebra), join)
          case StrokeDash(image, pattern) =>
            algebra.strokeDash(compile(image)(algebra), pattern)
          case FillColor(image, color) =>
            algebra.fillColor(compile(image)(algebra), color)
          case FillGradient(image, gradient) =>
            algebra.fillGradient(compile(image)(algebra), gradient)
          case NoStroke(image) =>
            algebra.noStroke(compile(image)(algebra))
          case NoFill(image) =>
            algebra.noFill(compile(image)(algebra))

          case Debug(image, color) =>
            algebra.debug(compile(image)(algebra), color)

          case Empty =>
            algebra.empty
        }
    }
  }
}
