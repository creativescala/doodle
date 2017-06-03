package doodle
package examples

import doodle.core._

object OpenClosedPaths {
  import Point._
  import PathElement._

  val openCurve =
      Image.openPath(
        List(curveTo(cartesian(50, 100), cartesian(100, 100), cartesian(150, 0)))
      )

  val closedCurve = openCurve.close

  val openTriangle =
      Image.openPath(List(
                       lineTo(cartesian(50, 100)),
                       lineTo(cartesian(100, 0)),
                       lineTo(cartesian(0, 0))
                     ))

  val closedTriangle = openTriangle.close

  def dropShadow(image: Image): Image =
    image.lineColor(Color.cornflowerBlue).lineWidth(10.0).
      on(image.lineColor(Color.black).lineWidth(10.0).at(10, 10))

  val image =
    dropShadow(openCurve).beside(dropShadow(closedCurve)).
      above(dropShadow(openTriangle).beside(dropShadow(closedTriangle)))
}
