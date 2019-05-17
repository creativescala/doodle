package doodle
package svg
package algebra

import cats.implicits._
import doodle.algebra.generic._
import doodle.core.{PathElement,Transform=>Tx}
import scalatags.generic.{Bundle, TypedTag}
import doodle.svg.effect.Svg

trait Path[Builder, Output <: FragT, FragT] extends GenericPath[(TypedTag[Builder, Output, FragT], ?)] {
  def bundle: Bundle[Builder, Output, FragT]

  object PathApi extends PathApi {
    val b = bundle
    import b.implicits._
    import b.{svgTags => svg}
    import b.svgAttrs

    def closedPath(tx: Tx, fill: Option[Fill], stroke: Option[Stroke], elements: List[PathElement]):(TypedTag[Builder, Output, FragT], Unit) = {
      val dAttr = Svg.toSvgPath(elements, Svg.Closed)
      val style = (fill.map(Svg.toStyle _) |+| stroke.map(Svg.toStyle _)).getOrElse("")
      val elt = svg.path(svgAttrs.transform:=Svg.toSvgTransform(tx), svgAttrs.style:=style, svgAttrs.d:=dAttr)

      (elt, ())
    }

    def openPath(tx: Tx, fill: Option[Fill], stroke: Option[Stroke], elements: List[PathElement]): (TypedTag[Builder, Output, FragT], Unit) = {
      val dAttr = Svg.toSvgPath(elements, Svg.Open)
      val style = (fill.map(Svg.toStyle _) |+| stroke.map(Svg.toStyle _)).getOrElse("")
      val elt = svg.path(svgAttrs.transform:=Svg.toSvgTransform(tx), svgAttrs.style:=style, svgAttrs.d:=dAttr)

      (elt, ())
    }
  }
}
