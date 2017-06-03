package doodle
package examples

import doodle.core._

object Layout {
  // Examples for debugging layout

  import doodle.core.Point._
  import doodle.core.PathElement._

  def addOrigin(image: Image): Image = {
    val origin = Image.circle(5).noLine.fillColor(Color.red)
    origin on image
  }

  def boundingBox(w: Double, h: Double, at: Vec) = {
    Image.rectangle(w, h).lineColor(Color.red).noFill.at(at)
  }

  // Examples of paths that are not centered in their bounding box
  val triangle =
    addOrigin(
      Image.openPath(List(
                       lineTo(cartesian(50, 100)),
                       lineTo(cartesian(100, 0)),
                       lineTo(cartesian(0, 0))
                     ))
    ).on(boundingBox(100, 100, Vec(50, 50)))

  val curve =
    addOrigin(
      Image.openPath(
        List(curveTo(cartesian(50, 100), cartesian(100, 100), cartesian(150, 0)))
      )
    ).on(boundingBox(150, 100, Vec(75, 50)))

  val vertical = triangle above (addOrigin(Image.circle(100)).on(boundingBox(200, 200, Vec.zero)))
  val horizontal = triangle beside (addOrigin(Image.circle(100)).on(boundingBox(200, 200, Vec.zero)))
}
