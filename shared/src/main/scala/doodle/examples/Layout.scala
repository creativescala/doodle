package doodle
package examples

import doodle.core._

object Layout {
  // Examples for debugging layout

  import doodle.core.Image._
  import doodle.core.Point._

  def addOrigin(image: Image): Image = {
    val origin = Circle(5).noLine.fillColor(Color.red)
    origin on image
  }

  // Examples of paths that are not centered in their bounding box
  val triangle =
    addOrigin(
      openPath(List(
                 LineTo(cartesian(50, 100)),
                 LineTo(cartesian(100, 0)),
                 LineTo(cartesian(0, 0))
               ))
    )

  val curve =
    addOrigin(
      openPath(
        List(BezierCurveTo(cartesian(50, 100), cartesian(100, 100), cartesian(150, 0)))
      )
    )


  val vertical = triangle above (addOrigin(circle(100))) above curve
  val horizontal = triangle beside (addOrigin(circle(100))) beside curve
}
