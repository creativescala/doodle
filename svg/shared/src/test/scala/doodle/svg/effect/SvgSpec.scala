package doodle
package svg
package effect

import doodle.algebra.generic._
import doodle.algebra.generic.reified.Reified
import doodle.core._
import org.scalacheck._
import org.scalacheck.Prop._
import scalatags.Text
import scala.collection.mutable.ListBuffer

object SvgSpec extends Properties("SVG Properties") {
  import Text.{svgAttrs, svgTags}
  import Text.implicits._

  val svg = Svg(Text)
  val blackStroke = Stroke(Color.black, 1.0, Cap.butt, Join.miter, None)
  val positiveDouble = Gen.choose(0.0, 1000.0)

  property("circle renders to svg circle") =
    forAll(positiveDouble){ (diameter: Double) =>
      val circle = Reified.strokeCircle(Transform.identity, blackStroke, diameter)
      val elt = new ListBuffer[Text.Tag]()
      val expected = svgTags.circle(
        svgAttrs.transform:=Svg.toSvgTransform(Transform.identity),
        svgAttrs.style:=Svg.toStyle(blackStroke),
        svgAttrs.r:=(diameter/2.0))
      circle.render(elt, Transform.identity)(svg.context)
      elt.head.render ?= expected.render
    }

  property("paths of path elements render correctly") = {
    import doodle.core.PathElement._
    (Svg.toSvgPath(List(moveTo(5,5), lineTo(10, 10), curveTo(20, 20, 30, 30, 40, 40)), Svg.Open) ?=
        "M 0,0 M 5,5 L 10,10 C 20,20 30,30 40,40 ") &&
    (Svg.toSvgPath(List(moveTo(5,5), lineTo(10, 10), curveTo(20, 20, 30, 30, 40, 40)), Svg.Closed) ?=
       "M 0,0 M 5,5 L 10,10 C 20,20 30,30 40,40 Z")
  }

  property("paths of points render correctly") = {
    (Svg.toSvgPath(Array(Point(5,5), Point(10, 10), Point(20, 20)), Svg.Open) ?=
        "M 5,5 L 10,10 L 20,20 ") &&
    (Svg.toSvgPath(Array(Point(5,5), Point(10, 10), Point(20, 20)), Svg.Closed) ?=
       "M 5,5 L 10,10 L 20,20 Z")
  }
}
