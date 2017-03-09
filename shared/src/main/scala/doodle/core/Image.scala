package doodle
package core

import doodle.core.font.Font

sealed abstract class Image extends Product with Serializable {
  import Image._

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

  def lineColor(color: Color): Image =
    ContextTransform(_.lineColor(color), this)

  def lineColorTransform(f: Color => Color): Image =
    ContextTransform(_.lineColorTransform(f), this)

  def lineWidth(width: Double): Image =
    ContextTransform(_.lineWidth(width), this)

  def fillColor(color: Color): Image =
    ContextTransform(_.fillColor(color), this)

  def fillColorTransform(f: Color => Color): Image =
    ContextTransform(_.fillColorTransform(f), this)

  def noLine: Image =
    ContextTransform(_.noLine, this)

  def noFill: Image =
    ContextTransform(_.noFill, this)

  def font(font: Font): Image =
    ContextTransform(_.font(font), this)


  // Affine Transform -------------------------------------------------

  def transform(tx: core.transform.Transform): Image =
    Transform(tx, this)

  def rotate(angle: Angle): Image =
    this.transform(core.transform.Transform.rotate(angle))

  def at(vec: Vec): Image =
    Transform(core.transform.Transform.translate(vec.x, vec.y), this)

  def at(x: Double, y: Double): Image =
    Transform(core.transform.Transform.translate(x, y), this)
}
sealed abstract class Path extends Image {
  import Image._

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
  final case class OpenPath(elements: List[PathElement]) extends Path
  final case class ClosedPath(elements: List[PathElement]) extends Path
  final case class Text(get: String) extends Image
  final case class Circle(r: Double) extends Image
  final case class Rectangle(w: Double, h: Double) extends Image
  final case class Triangle(w: Double, h: Double) extends Image
  final case class Beside(l: Image, r: Image) extends Image
  final case class Above(l: Image, r: Image) extends Image
  final case class On(t: Image, b: Image) extends Image
  final case class Transform(tx: transform.Transform, i: Image) extends Image
  final case class ContextTransform(f: DrawingContext => DrawingContext, image: Image) extends Image
  final case object Empty extends Image

  // Smart constructors

  def closedPath(elements: Seq[PathElement]): Path = {
    // Paths must start at the origin. Thus we always move to the origin to
    // start.
    ClosedPath((PathElement.moveTo(0,0) +: elements).toList)
  }


  def openPath(elements: Seq[PathElement]): Path = {
    // Paths must start at the origin. Thus we always move to the origin to
    // start.
    OpenPath((PathElement.moveTo(0,0) +: elements).toList)
  }

  def text(characters: String): Image =
    Text(characters)

  def line(x: Double, y: Double): Image = {
    val startX = -x/2
    val startY = -y/2
    val endX = x/2
    val endY = y/2
    openPath(
      List(
        PathElement.moveTo(startX, startY),
        PathElement.lineTo(endX, endY)
      )
    )
  }

  def circle(r: Double): Image =
    Circle(r)

  def rectangle(w: Double, h: Double): Image =
    Rectangle(w,h)

  def square(side: Double): Image =
    rectangle(side, side)

  def regularPolygon(sides: Int, radius: Double, angle: Angle): Image = {
    import PathElement._

    val rotation = Angle.one / sides
    val path =
      (1 to sides).map { n =>
          lineTo(radius, rotation * n + angle)
      }.toList

    closedPath(moveTo(radius, angle) +: path)
  }

  def star(points: Int, outerRadius: Double, innerRadius: Double, angle: Angle): Image = {
    import PathElement._

    val rotation = Angle.one / (points * 2)
    val path =
      (1 to (points * 2)).map { n =>
        if(n % 2 == 0)
          lineTo(outerRadius, rotation * n + angle)
        else
          lineTo(innerRadius, rotation * n + angle)
      }.toList

    closedPath(moveTo(outerRadius, angle) +: path)
  }

  def rightArrow(w: Double, h: Double): Image = {
    import PathElement._

    val path = List(
      moveTo(w/2, 0),
      lineTo(0, h/2),

      lineTo(0, h * 0.2),
      lineTo(-w/2, h * 0.2),
      lineTo(-w/2, -h * 0.2),
      lineTo(0, -h * 0.2),

      lineTo(0, -h/2),
      lineTo(w/2, 0)
    )

    closedPath(path)
  }

  def roundedRectangle(w: Double, h: Double, r: Double): Image = {
    import PathElement._

    // Clamp radius to the smallest of width and height
    val radius =
      if(r > w/2 || r > h/2)
        (w/2) min (h/2)
      else
        r

    // Magic number of drawing circles with bezier curves
    // See http://spencermortensen.com/articles/bezier-circle/ for approximation
    // of a circle with a Bezier curve.
    val c = (4.0/3.0) * (Math.sqrt(2) - 1)
    val cR = c * radius

    val elts = List(
      moveTo(w/2 - radius, h/2),
      curveTo(w/2 - radius + cR, h/2,
              w/2, h/2 - radius + cR,
              w/2, h/2 - radius),
      lineTo(w/2, -h/2 + radius),
      curveTo(w/2, -h/2 + radius - cR,
              w/2 - radius + cR, -h/2,
              w/2 - radius, -h/2),
      lineTo(-w/2 + radius, -h/2),
      curveTo(-w/2 + radius - cR, -h/2,
              -w/2, -h/2 + radius - cR,
              -w/2, -h/2 + radius),
      lineTo(-w/2, h/2 - radius),
      curveTo(-w/2, h/2 - radius + cR,
              -w/2 + radius - cR, h/2,
              -w/2 + radius, h/2),
      lineTo(w/2 - radius, h/2)
    )

    closedPath(elts)
  }

  def triangle(w: Double, h: Double): Image =
    Triangle(w,h)

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
  def catmulRom(points: Seq[Point], tension: Double = 0.5): Path = {
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
        ((-tension * pt0.x) + 3*pt1.x + (tension * pt2.x)) / 3.0,
        ((-tension * pt0.y) + 3*pt1.y + (tension * pt2.y)) / 3.0,

        ((tension * pt1.x) + 3*pt2.x - (tension * pt3.x)) / 3.0,
        ((tension * pt1.y) + 3*pt2.y - (tension * pt3.y)) / 3.0,

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

    points.headOption.fold(OpenPath(List.empty)){ pt0 =>
      OpenPath(PathElement.moveTo(pt0) :: iter(pt0 :: points.toList))
    }
  }

  def empty: Image =
    Empty
}
