package doodle
package svg
package effect

import doodle.algebra.generic._
import doodle.core._
import doodle.language.Basic
import org.scalacheck._
import org.scalacheck.Prop._
import scala.collection.mutable

object SvgSpec
    extends Properties("SVG Properties")
    with doodle.svg.algebra.TestAlgebraModule {
  import scalatags.Text.{svgAttrs, svgTags}
  import scalatags.Text.implicits._

  val blackStroke = Stroke(Color.black, 1.0, Cap.butt, Join.miter, None)
  val positiveDouble = Gen.choose(0.0, 1000.0)

  property("circle renders to svg circle") = forAll(positiveDouble) {
    (diameter: Double) =>
      val circle = doodle.algebra.Picture[Basic, Drawing, Unit](
        algebra => algebra.strokeColor(algebra.circle(diameter), Color.black)
      )
      val (_, elt, _) =
        Svg.renderWithoutRootTag(algebraInstance, circle).unsafeRunSync()
      val expected =
        svgTags.g(
          svgTags.defs(),
          svgTags.circle(
            svgAttrs.transform := Svg.toSvgTransform(
              Transform.verticalReflection
            ),
            svgAttrs.style := Svg
              .toStyle(Some(blackStroke), None, mutable.Set.empty),
            svgAttrs.r := (diameter / 2.0)
          )
        )
      elt ?= expected
  }

  property("paths of path elements render correctly") = {
    import doodle.core.PathElement._
    (Svg.toSvgPath(
      List(moveTo(5, 5), lineTo(10, 10), curveTo(20, 20, 30, 30, 40, 40)),
      Svg.Open
    ) ?=
      "M 0,0 M 5,5 L 10,10 C 20,20 30,30 40,40 ") &&
    (Svg.toSvgPath(
      List(moveTo(5, 5), lineTo(10, 10), curveTo(20, 20, 30, 30, 40, 40)),
      Svg.Closed
    ) ?=
      "M 0,0 M 5,5 L 10,10 C 20,20 30,30 40,40 Z")
  }

  property("paths of points render correctly") = {
    (Svg.toSvgPath(Array(Point(5, 5), Point(10, 10), Point(20, 20)), Svg.Open) ?=
      "M 5,5 L 10,10 L 20,20 ") &&
    (Svg.toSvgPath(Array(Point(5, 5), Point(10, 10), Point(20, 20)), Svg.Closed) ?=
      "M 5,5 L 10,10 L 20,20 Z")
  }
}
