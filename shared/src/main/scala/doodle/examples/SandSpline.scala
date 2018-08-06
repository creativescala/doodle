package doodle
package examples

import scala.math.BigDecimal
import cats.implicits._
import doodle.core._
import doodle.syntax._
import doodle.random._
import doodle.backend.Canvas

object SandSpline {
  val color = Color.darkSlateBlue.alpha(0.01.normalized)
  val dc = DrawingContext.empty.fillColor(color)

  def dot(canvas: Canvas, location: Point): Unit =
    canvas.circle(
      dc,
      location.x,
      location.y,
      0.5
    )

  val samples = 400
  /* Draw one sand spline */
  def sandSpline(canvas: Canvas, pts: List[Point]): Unit = {
    val spline = Parametric.interpolate(pts)
    def sand(t: Normalized): Unit = {
      val loc = spline(t)
      dot(canvas, loc)
    }

    def loop(count: Int): Unit =
      count match {
        case 0 => sand(0.normalized)
        case n =>
          val t = (n.toDouble / samples).normalized
          sand(t)
          loop(n-1)
      }

    loop(samples)
  }

  def noise(t: Normalized): Random[Vec] = {
    val stdDev = t.get * 80
    val noise = Random.normal(0.0, stdDev)
    (noise, noise).mapN((x, y) => Vec(x, y))
  }

  def perturb(t: Normalized, pt: Point): Random[Point] =
    noise(t).map(v => pt + v)

  /*
   * Draw `count` sand splines, where the points for each spline are generated from a parametric curve distorted by increasing amounts of noise
   */
  def sandSplines(canvas: Canvas, curve: Normalized => Point, count: Int = 400): Random[List[Point]] = {
    def sample: List[Point] =
      (BigDecimal(0.0) to 1.0 by 0.02).foldLeft(List.empty[Point]){ (accum, t) =>
        curve(t.doubleValue.normalized) :: accum
      }

    def step(pts: List[Point]): Random[List[Point]] =
      pts.zipWithIndex.map { case (pt, i) =>
        perturb((i.toDouble / count).normalized, pt)
      }.sequence

    def iter(count: Int, last: Random[List[Point]]): Random[List[Point]] =
      count match {
        case 0 => last
        case n =>
          val next =
            for {
              pts  <- last
              _     = sandSpline(canvas, pts)
              next <- step(pts)
            } yield next
          iter(n-1, next)
      }

    iter(count, Random.always(sample))
  }

  def ofSize(count: Int): Image =
    Image.draw(400, 400){ canvas =>
      sandSplines(canvas, Parametric.circle(100).toNormalizedCurve, count).run
    } on Image.square(400).fillColor(Color.black)

  val image =
    ofSize(10000)

}
