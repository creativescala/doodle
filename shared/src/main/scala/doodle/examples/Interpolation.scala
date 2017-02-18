package doodle
package examples

import doodle.core._
import doodle.core.Image._
import doodle.syntax._

object Interpolation {
  val pts =
    for(x <- 1 to 400 by 20) yield Point.cartesian(x, (x / 100.0).turns.sin * 100)

  val dot =
    circle(5).fillColor(Color.red)

  val dots =
    allOn(pts.map { pt => dot at pt.toVec })

  val default =
    interpolatingSpline(pts).lineColor(Color.cornflowerBlue).lineWidth(3.0)

  val tight =
    catmulRom(pts, 1.0).lineColor(Color.cornflowerBlue).lineWidth(3.0)

  val loose =
    catmulRom(pts, 0.0).lineColor(Color.cornflowerBlue).lineWidth(3.0)

  val image = (default on dots) above (tight on dots) above (loose on dots)
}

