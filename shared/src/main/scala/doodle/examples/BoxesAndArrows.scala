package doodle
package examples

import doodle.core._
import doodle.core.Image._
import doodle.core.font._
import doodle.syntax._
import doodle.backend.StandardInterpreter._

object BoxesAndArrows {
  import Point._
  import FontFamily._
  import FontFace._
  import FontSize._

  val size = 100

  val spacer = rectangle(size * 0.2,size).noFill.noLine

  val box = roundedRectangle(size, size, size*.12).lineWidth(size*.12).noFill 

  val font = Font(SansSerif, Normal, Points(size/2))
  val equals = text("=").font(font)

  val c = circle(size * 0.3)
  val t = triangle(size * 0.6, size * 0.6)

  val circleBox = box on c
  val triangleBox = box on t
  val circleAndTriangleBox = box on (circle(size * 0.15) beside triangle(size * 0.3, size * 0.3))

  val circleToTriangle =
    c beside spacer beside rightArrow(size, size) beside spacer beside t

  def besideWithSpace(elts: List[Image]): Image =
    elts.foldLeft(Image.empty){ (accum, elt) =>
      accum beside spacer beside elt
    }

  val map: Image =
    besideWithSpace(
      List(
        circleBox, text("map").font(font), circleToTriangle, equals, triangleBox
      )
    )

  val applicative: Image =
    besideWithSpace(
      List(
        circleBox, text("|@|").font(font), triangleBox, equals, circleAndTriangleBox
      )
    )
}
