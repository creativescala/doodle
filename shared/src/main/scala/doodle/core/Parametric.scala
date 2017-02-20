package doodle
package core

import doodle.syntax._
import scala.annotation.tailrec


trait Parametric[A] extends (A => Point) {
  /** Sample `count` points uniformly along this parametric curve */
  def sample(count: Int): List[Point]
}

/**
  * A collection of parametric curves.
  *
  * A parametric curve is a function from some input---usually a normalized
  * number or an angle---to a `Point`.
  */
object Parametric {

  /**
    * A parametric curve that maps angles to points
    */
  final case class AngularCurve(f: Angle => Point) extends Parametric[Angle] {
    def apply(angle: Angle): Point = f(angle)

    def toNormalizedCurve(max: Angle): NormalizedCurve =
      NormalizedCurve((t: Normalized) => f((t.get * max.toRadians).radians))

    def toNormalizedCurve: NormalizedCurve =
      toNormalizedCurve(Angle.one)

    def sample(count: Int, max: Angle): List[Point] = {
      val step = max.toRadians / count

      def loop(count: Int, accum: List[Point]): List[Point] =
        count match {
          case 0 => accum
          case n => loop(n-1, f((n * step).radians) :: accum)
        }

      loop(count, List.empty)
    }

    def sample(count: Int): List[Point] =
      sample(count, Angle.one)
  }

  /**
    * A parametric curve that maps normalized to points
    */
  final case class NormalizedCurve(f: Normalized => Point) extends Parametric[Normalized] {
    def apply(t: Normalized): Point = f(t)

    /** Convert to an `AngularCurve` where the angle ranges from 0 to 360 degrees */
    def toAngularCurve: AngularCurve =
      AngularCurve((theta: Angle) => f((theta.toTurns).normalized))

    /** Sample `count` points uniformly along this parametric curve */
    def sample(count: Int): List[Point] = {
      val step = 1.0 / count

      def loop(count: Int, accum: List[Point]): List[Point] =
        count match {
          case 0 => accum
          case n => loop(n-1, f((n * step).normalized) :: accum)
        }

      loop(count, List.empty)
    }
  }

  /** A circle */
  def circle(radius: Double): AngularCurve =
    AngularCurve((theta: Angle) => Point(radius, theta))

  /** Rose curve */
  def rose(k: Double, scale: Double = 1.0): AngularCurve =
    AngularCurve((theta: Angle) => Point(scale * (theta * k).cos, theta))

  /** Logarithmic spiral */
  def logarithmicSpiral(a: Double, b: Double): AngularCurve =
    AngularCurve((theta: Angle) => Point(a * Math.exp(theta.toRadians * b), theta))

  /** Quadratic bezier curve */
  def quadraticBezier(start: Point, cp: Point, end: Point): NormalizedCurve = {
    // We don't use de Casteljau's algorithm because I don't think numerical stability is important for the applications we have in mind. I reserve the right to change this opinion
    NormalizedCurve(
      (t: Normalized) => {
        val tD = t.get
        val p1 = start.toVec * (1 - tD) * (1 - tD)
        val p2 = cp.toVec * (2 * (1 - tD) * tD)
        val p3 = end.toVec * (tD * tD)

        (p1 + p2 + p3).toPoint
      }
    )
  }

  def cubicBezier(start: Point, cp1: Point, cp2: Point, end: Point): NormalizedCurve = {
    NormalizedCurve(
      (t: Normalized) => {
        val tD = t.get
        val oneMinusTD = 1 - tD
        val p0 = start.toVec * (oneMinusTD * oneMinusTD * oneMinusTD)
        val p1 = cp1.toVec * (3 * oneMinusTD * oneMinusTD * tD)
        val p2 = cp2.toVec * (3 * oneMinusTD * tD * tD)
        val p3 = end.toVec * (tD * tD * tD)

        (p0 + p1 + p2 + p3).toPoint
      }
    )
  }

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
  def interpolate(points: Seq[Point], tension: Double = 0.5): NormalizedCurve = {
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
    def toCurve(pt0: Point, pt1: Point, pt2: Point, pt3: Point): NormalizedCurve =
      cubicBezier(
        pt1,

        Point(
          ((-tension * pt0.x) + 3*pt1.x + (tension * pt2.x)) / 3.0,
          ((-tension * pt0.y) + 3*pt1.y + (tension * pt2.y)) / 3.0
        ),

        Point(
          ((tension * pt1.x) + 3*pt2.x - (tension * pt3.x)) / 3.0,
          ((tension * pt1.y) + 3*pt2.y - (tension * pt3.y)) / 3.0
        ),

        pt2
      )


    @tailrec
    def iter(points: Seq[Point], accum: Seq[NormalizedCurve]): Seq[NormalizedCurve] = {
      points match {
        case pt0 +: pt1 +: pt2 +: pt3 +: pts =>
          iter(
            (pt1 +: pt2 +: pt3 +: pts),
            toCurve(pt0, pt1, pt2, pt3) +: accum
          )

        case pt0 +: pt1 +: pt2 +: Seq() =>
          // Case where we've reached the end of the sequence of points
          // We repeat the last point
          val pt3 = pt2
          toCurve(pt0, pt1, pt2, pt3) +: accum

        case _ =>
          // There were two or fewer points in the sequence
          accum
      }
    }

    val curves = iter(points, List.empty).reverse.toArray
    val size = curves.size

    /* Get the index into the curves array from t, where each curve has a 1/size share of the space */
    def index(t: Normalized): Int = {
      if(t.get == 1.0)
        size - 1
      else
        Math.floor(t.get * size).toInt
    }

    NormalizedCurve {
      (t: Normalized) => {
        val curve = curves(index(t))
        val offset = (t.get * size) - index(t)
        curve(offset.normalized)
      }
    }
  }
}
