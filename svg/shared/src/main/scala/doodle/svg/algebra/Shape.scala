package doodle
package svg
package algebra

import doodle.algebra.generic._
import doodle.core.{Point, Transform => Tx}

import scala.collection.mutable

trait ShapeModule { self: Base with SvgModule =>
  trait Shape extends GenericShape[SvgResult] {
    object ShapeApi extends ShapeApi {
      val b = bundle
      import b.implicits._
      import b.{svgAttrs, svgTags}

      def rectangle(tx: Tx,
                    fill: Option[Fill],
                    stroke: Option[Stroke],
                    width: Double,
                    height: Double): SvgResult[Unit] = {
        val x = -(width / 2.0)
        val y = -(height / 2.0)
        val set = mutable.Set.empty[self.Tag]
        val style = Svg.toStyle(stroke, fill, set)
        val elt = svgTags.rect(svgAttrs.transform := Svg.toSvgTransform(tx),
                               svgAttrs.style := style,
                               svgAttrs.x := x,
                               svgAttrs.y := y,
                               svgAttrs.width := width,
                               svgAttrs.height := height)

        (elt, set, ())
      }

      def triangle(tx: Tx,
                   fill: Option[Fill],
                   stroke: Option[Stroke],
                   width: Double,
                   height: Double): SvgResult[Unit] = {
        val w = width / 2.0
        val h = height / 2.0
        val points = Array(Point(-w, -h), Point(0, h), Point(w, -h))
        val dAttr = Svg.toSvgPath(points, Svg.Closed)
        val set = mutable.Set.empty[self.Tag]
        val style = Svg.toStyle(stroke, fill, set)
        val elt = svgTags.path(svgAttrs.transform := Svg.toSvgTransform(tx),
                               svgAttrs.style := style,
                               svgAttrs.d := dAttr)

        (elt, set, ())
      }

      def circle(tx: Tx,
                 fill: Option[Fill],
                 stroke: Option[Stroke],
                 diameter: Double): SvgResult[Unit] = {
        val set = mutable.Set.empty[self.Tag]
        val style = Svg.toStyle(stroke, fill, set)
        val elt = svgTags.circle(svgAttrs.transform := Svg.toSvgTransform(tx),
                                 svgAttrs.style := style,
                                 svgAttrs.r := (diameter / 2.0))

        (elt, set, ())
      }

      def unit: SvgResult[Unit] = {
        (svgTags.g(), mutable.Set.empty, ())
      }
    }
  }
}
