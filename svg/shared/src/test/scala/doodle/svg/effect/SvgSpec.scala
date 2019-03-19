package doodle
package svg
package effect

import doodle.algebra.generic._
import doodle.core._
import org.scalacheck._
import org.scalacheck.Prop._
import scalatags.Text
import scala.collection.mutable.ListBuffer

object SvgSpec extends Properties("SVG Properties") {
  import Text.{svgAttrs, svgTags}
  import Text.implicits._

  val svg = Svg(Text)
  val blackStroke = Stroke(Color.black, 1.0, Cap.butt, Join.miter)
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
}
