package doodle
package examples

import doodle.core._
import doodle.syntax._

object CreativeScala {
  // Images from Creative Scala

  import doodle.core.Image._
  import doodle.core.Point._
  import doodle.core.Color._

  val triangle =
    OpenPath(List(
               LineTo(cartesian(50, 100)),
               LineTo(cartesian(100, 0)),
               LineTo(cartesian(0, 0))
             )) 

  val curve =
    OpenPath(List(BezierCurveTo(cartesian(50, 100), cartesian(100, 100), cartesian(150, 0))))


  val openPaths =
    (triangle beside curve) lineWidth 6.0 lineColor royalBlue fillColor skyBlue
  val closedPaths =
    (triangle.close beside curve.close) lineWidth 6.0 lineColor royalBlue fillColor skyBlue

  val paths = openPaths above closedPaths
}
