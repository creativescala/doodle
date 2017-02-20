package doodle
package examples

import doodle.core._
import doodle.syntax._

object ParametricSamples {
  val color = Color.lightSlateGray.alpha(0.3.normalized)
  val dot = Image.circle(2).fillColor(color).noLine

  def render[A](curve: Parametric[A], count: Int): Image =
    allOn((curve.sample(count)) map { pt => dot.at(pt.toVec) })

  def circle(count: Int) =
    render(Parametric.circle(200), count)

  def rose(count: Int) =
    render(Parametric.rose(3, 200), count)

  def logarithmicSpiral(count: Int) =
    render(Parametric.logarithmicSpiral(1, 0.25).toNormalizedCurve(1440.degrees), count)

  def bezier(count: Int) =
    render(Parametric.quadraticBezier(Point.zero, Point(100, 200), Point(200, 0)), count)

  def interpolate[A](count: Int, f: Parametric[A]) =
    Parametric.interpolate(f.sample(count))

  def interpolatedCircle(count: Int, interps: Int) =
    render(interpolate(interps, Parametric.circle(200)), count)

}
