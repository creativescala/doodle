package doodle
package examples

import cats.implicits._
import doodle.core._
import doodle.syntax._
import doodle.random._

object SandSpline {
  val color = Color.lightSkyBlue.alpha(0.01.normalized)
  val dot = Image.circle(0.5).fillColor(color).noLine


  val samples = 200
  /* Draw one sand spline */
  def sandSpline(pts: List[Point]): Image = {
    val spline = Parametric.interpolate(pts)
    def sand(t: Normalized): Image =
      dot.at(spline(t).toVec)

    def iter(count: Int, accum: Image): Image =
      count match {
        case 0 => sand(0.normalized) on accum
        case n =>
          val t = (n.toDouble / samples).normalized
          iter(n-1, sand(t) on accum)
      }

    iter(samples, Image.empty)
  }

  def noise(t: Normalized): Random[Vec] = {
    val stdDev = t.get * 20
    val noise = Random.normal(0.0, stdDev)
    (noise |@| noise).map((x, y) => Vec(x, y))
  }

  def perturb(t: Normalized, pt: Point): Random[Point] =
    noise(t).map(v => pt + v)

  /*
   * Draw `count` sand splines, where the points for each spline are generated from a parametric curve distorted by increasing amounts of noise
   */
  def sandSplines(curve: Normalized => Point, count: Int = 400): Random[Image] = {
    def sample: List[Point] =
      (0.0 to 1.0 by 0.02).foldLeft(List.empty[Point]){ (accum, t) =>
        curve(t.normalized) :: accum
      }

    def step(pts: List[Point]): Random[List[Point]] =
      pts.zipWithIndex.map { case (pt, i) =>
        perturb((i.toDouble / count).normalized, pt)
      }.sequence

    def iter(count: Int, accum: Random[List[List[Point]]]): Random[List[List[Point]]] =
      count match {
        case 0 => accum
        case n =>
          val nextAccum =
            for {
              pts <- accum
              ps  <- step(pts.head)
            } yield ps :: pts
          iter(n-1, nextAccum)
        }

    iter(count, Random.always(List(sample))).map(pts => allOn(pts.map(sandSpline _)))
  }

  def ofSize(count: Int) =
    sandSplines(Parametric.circle(100).toNormalizedCurve, count)

  val image =
    ofSize(400).map{ img => img on Image.square(400).fillColor(Color.black)}

}
