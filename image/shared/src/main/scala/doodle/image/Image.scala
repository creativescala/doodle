package doodle
package image

import doodle.core._
import doodle.language.Basic

sealed abstract class Image extends Product with Serializable {
  import Image.Elements._

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

  // Context Transform ------------------------------------------------

  def strokeColor(color: Color): Image =
    StrokeColor(this, color)

  // def strokeColorTransform(f: Color => Color): Image =
  //   ContextTransform(_.strokeColorTransform(f), this)

  def strokeWidth(width: Double): Image =
    StrokeWidth(this, width)

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

  // def font(font: Font): Image =
  //   ContextTransform(_.font(font), this)

  // Affine Transform -------------------------------------------------

  def transform(tx: core.Transform): Image =
    Transform(tx, this)

  def rotate(angle: Angle): Image =
    this.transform(core.Transform.rotate(angle))

  def scale(x: Double, y: Double): Image =
    this.transform(core.Transform.scale(x, y))

  def at(vec: Vec): Image =
    // Transform(core.transform.Transform.translate(vec.x, vec.y), this)
    At(this, vec.x, vec.y)

  def at(pt: Point): Image =
    At(this, pt,x, pt.y)

  def at(x: Double, y: Double): Image =
    // Transform(core.transform.Transform.translate(x, y), this)
    At(this, x, y)

  def at(r: Double, a: Angle): Image = {
    val pt = Point(r, a)
    At(this, pt.x, pt.y)
  }

  // Convert to tagless final format

  def compile[Algebra[x[?]] <: Basic[x], F[_]]
    : doodle.algebra.Picture[Algebra, F, Unit] =
    Image.compile(this)
}
sealed abstract class Path extends Image {
  import Image.Elements._

  def isOpen: Boolean =
    this match {
      case OpenPath(_)   => true
      case ClosedPath(_) => false
    }

  def isClosed: Boolean =
    !this.isOpen

  def open: Path =
    this match {
      case OpenPath(_)      => this
      case ClosedPath(elts) => OpenPath(elts)
    }

  def close: Path =
    this match {
      case OpenPath(elts) => ClosedPath(elts)
      case ClosedPath(_)  => this
    }
}
object Image {

  /** Contains the leaves of the Image algebraic data type. Packaged here so they
    * don't pollute the namespace when importing Image to access to the smart
    * constructors. */
  object Elements {
    final case class OpenPath(elements: List[PathElement]) extends Path
    final case class ClosedPath(elements: List[PathElement]) extends Path
    // final case class Text(get: String) extends Image
    final case class Circle(d: Double) extends Image
    final case class Rectangle(w: Double, h: Double) extends Image
    final case class Triangle(w: Double, h: Double) extends Image
    // final case class Draw(w: Double, h: Double, f: Canvas => Unit) extends Image
    final case class Beside(l: Image, r: Image) extends Image
    final case class Above(l: Image, r: Image) extends Image
    final case class On(t: Image, b: Image) extends Image
    final case class At(image: Image, x: Double, y: Double) extends Image
    final case class Transform(tx: core.Transform, i: Image) extends Image
    // Style
    final case class StrokeWidth(image: Image, width: Double) extends Image
    final case class StrokeColor(image: Image, color: Color) extends Image
    final case class FillColor(image: Image, color: Color) extends Image
    final case class FillGradient(image: Image, gradient: Gradient) extends Image
    final case class NoStroke(image: Image) extends Image
    final case class NoFill(image: Image) extends Image

    final case object Empty extends Image
  }
  import Elements._

  // Smart constructors

  def closedPath(elements: Seq[PathElement]): Path = {
    // Paths must start at the origin. Thus we always move to the origin to
    // start.
    ClosedPath((PathElement.moveTo(0, 0) +: elements).toList)
  }

  def openPath(elements: Seq[PathElement]): Path = {
    // Paths must start at the origin. Thus we always move to the origin to
    // start.
    OpenPath((PathElement.moveTo(0, 0) +: elements).toList)
  }

  // def text(characters: String): Image =
  //   Text(characters)

  def line(x: Double, y: Double): Image = {
    val startX = -x / 2
    val startY = -y / 2
    val endX = x / 2
    val endY = y / 2
    openPath(
      List(
        PathElement.moveTo(startX, startY),
        PathElement.lineTo(endX, endY)
      )
    )
  }

  def circle(diameter: Double): Image =
    Circle(diameter)

  def rectangle(width: Double, height: Double): Image =
    Rectangle(width, height)

  def square(side: Double): Image =
    rectangle(side, side)

  def regularPolygon(sides: Int, radius: Double, angle: Angle): Image = {
    closedPath(PathElement.regularPolygon(sides, radius, angle))
  }

  def star(points: Int,
           outerRadius: Double,
           innerRadius: Double,
           angle: Angle): Image = {
    closedPath(PathElement.star(points, outerRadius, innerRadius, angle))
  }

  def rightArrow(width: Double, height: Double): Image = {
    import PathElement._

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

    closedPath(path)
  }

  def roundedRectangle(width: Double, height: Double, radius: Double): Image = {
    closedPath(PathElement.roundedRectangle(width, height, radius))
  }

  def triangle(width: Double, height: Double): Image =
    Triangle(width, height)

  /**
    * Construct an open path of bezier curves that intersects all the given
    * points. Defaults to `catmulRom` with the default tension.
    */
  def interpolatingSpline(points: Seq[Point]): Path =
    catmulRom(points)

  /**
    * Interpolate a spline (a curve) that passes through all the given points,
    * using the Catmul Rom formulation (see, e.g., https://en.wikipedia.org/wiki/Cubic_Hermite_spline)
    *
    * The tension can be changed to control how tightly the curve turns. It defaults to 0.5.
    *
    * The Catmul Rom algorithm requires a point before and after each pair of
    * points that define the curve. To meet this condition for the first and last
    * points in `points`, they are repeated.
    *
    * If `points` has less than two elements an empty `Path` is returned.
    */
  def catmulRom(points: Seq[Point], tension: Double = 0.5): Path =
    openPath(PathElement.catmulRom(points, tension))

//  def draw(width: Double, height: Double)(f: Canvas => Unit): Image =
//    Draw(width, height, f)

  val empty: Image =
    Empty

  /** Compile an `Image` to a `doodle.algebra.Picture` */
  def compile[Alg[x[_]] <: Basic[x], F[_]](
      image: Image): doodle.algebra.Picture[Alg, F, Unit] = {
    import cats.instances.unit._
    import Elements._

    doodle.algebra.Picture[Alg, F, Unit] { algebra =>
      image match {
        case OpenPath(elements) =>
          algebra.path(doodle.core.OpenPath(elements.reverse))
        case ClosedPath(elements) =>
          algebra.path(doodle.core.ClosedPath(elements.reverse))

        case Circle(d) =>
          algebra.circle(d)
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
        case At(image, x, y) =>
          algebra.at(compile(image)(algebra), x, y)

        case Transform(tx, i) =>
          algebra.transform(compile(i)(algebra), tx)

        case StrokeWidth(image, width) =>
          algebra.strokeWidth(compile(image)(algebra), width)
        case StrokeColor(image, color) =>
          algebra.strokeColor(compile(image)(algebra), color)
        case FillColor(image, color) =>
          algebra.fillColor(compile(image)(algebra), color)
        case FillGradient(image, gradient) =>
          algebra.fillGradient(compile(image)(algebra), gradient)
        case NoStroke(image) =>
          algebra.noStroke(compile(image)(algebra))
        case NoFill(image) =>
          algebra.noFill(compile(image)(algebra))

        case Empty =>
          algebra.empty
      }
    }
  }
}
