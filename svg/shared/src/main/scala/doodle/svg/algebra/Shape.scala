package doodle
package svg
package algebra

import cats.implicits._
import doodle.algebra.generic._
import doodle.core.{Point,Transform=>Tx }
import scalatags.generic.{Bundle, TypedTag}
import doodle.svg.effect.Svg

trait Shape[Builder, Output <: FragT, FragT] extends GenericShape[(TypedTag[Builder, Output, FragT], ?)] {
  def bundle: Bundle[Builder, Output, FragT]

  object ShapeApi extends ShapeApi {
    val b = bundle
    import b.implicits._
    import b.{svgTags => svg}
    import b.svgAttrs

    def rectangle(tx: Tx, fill: Option[Fill], stroke: Option[Stroke], width: Double, height: Double): (TypedTag[Builder, Output, FragT], Unit) = {
      val x = -(width / 2.0)
      val y = -(height / 2.0)
      val style = (fill.map(Svg.toStyle _) |+| stroke.map(Svg.toStyle _)).getOrElse("")
      val elt = svg.rect(svgAttrs.transform:=Svg.toSvgTransform(tx),
                         svgAttrs.style:=style,
                         svgAttrs.x:=x,
                         svgAttrs.y:=y,
                         svgAttrs.width:=width,
                         svgAttrs.height:=height)

      (elt, ())
    }

    def triangle(tx: Tx, fill: Option[Fill], stroke: Option[Stroke], width: Double, height: Double): (TypedTag[Builder, Output, FragT], Unit) = {
      val w = width / 2.0
      val h = height / 2.0
      val points = Array(Point(-w, -h), Point(0, h), Point(w, -h))
      val dAttr = Svg.toSvgPath(points, Svg.Closed)
      val style = (fill.map(Svg.toStyle _) |+| stroke.map(Svg.toStyle _)).getOrElse("")
      val elt = svg.path(svgAttrs.transform:=Svg.toSvgTransform(tx), svgAttrs.style:=style, svgAttrs.d:=dAttr)

      (elt, ())
    }

    def circle(tx: Tx, fill: Option[Fill], stroke: Option[Stroke], diameter: Double): (TypedTag[Builder, Output, FragT], Unit) = {
      val style = (fill.map(Svg.toStyle _) |+| stroke.map(Svg.toStyle _)).getOrElse("")
      val elt = svg.circle(svgAttrs.transform:=Svg.toSvgTransform(tx),
                           svgAttrs.style:=style,
                           svgAttrs.r:=(diameter/2.0))

      (elt, ())
    }

    def unit: (TypedTag[Builder, Output, FragT], Unit) = {
      (svg.g(), ())
    }
  }
}
