package doodle
package svg
package effect

import doodle.core._
import doodle.algebra.generic.{Fill, Stroke}
import doodle.algebra.generic.reified.GraphicsContext
import scalatags.generic.{Bundle, TypedTag}
import scala.collection.mutable.ListBuffer

final case class SvgGraphicsContext[Builder, Output <: FragT, FragT](bundle: Bundle[Builder, Output, FragT])
    extends GraphicsContext[ListBuffer[TypedTag[Builder, Output, FragT]]] {
  import bundle.implicits._
  import bundle.{svgTags => svg}
  import bundle.svgAttrs

  type Context = ListBuffer[TypedTag[Builder, Output, FragT]]

  def fillRect(gc: Context)(transform: Transform,
                            fill: Fill,
                            width: Double,
                            height: Double): Unit = {
    val x = -(width / 2.0)
    val y = -(height / 2.0)
    val style = Svg.toStyle(fill)
    val elt = svg.rect(svgAttrs.transform:=Svg.toSvgTransform(transform),
                       svgAttrs.style:=style,
                       svgAttrs.x:=x,
                       svgAttrs.y:=y,
                       svgAttrs.width:=width,
                       svgAttrs.height:=height)
    gc += elt
  }

  def strokeRect(gc: Context)(transform: Transform,
                              stroke: Stroke,
                              width: Double,
                              height: Double): Unit = {
    val x = -(width / 2.0)
    val y = -(height / 2.0)
    val style = Svg.toStyle(stroke)
    val elt = svg.rect(svgAttrs.transform:=Svg.toSvgTransform(transform),
                       svgAttrs.style:=style,
                       svgAttrs.x:=x,
                       svgAttrs.y:=y,
                       svgAttrs.width:=width,
                       svgAttrs.height:=height)
    gc += elt
  }

  def fillCircle(gc: Context)(transform: Transform, fill: Fill, diameter: Double): Unit = {
    val style = Svg.toStyle(fill)
    val elt = svg.circle(svgAttrs.transform:=Svg.toSvgTransform(transform),
                         svgAttrs.style:=style,
                         svgAttrs.r:=(diameter/2.0))
    gc += elt
  }
  def strokeCircle(gc: Context)(transform: Transform, stroke: Stroke, diameter: Double): Unit = {
    val style = Svg.toStyle(stroke)
    val elt = svg.circle(svgAttrs.transform:=Svg.toSvgTransform(transform),
                         svgAttrs.style:=style,
                         svgAttrs.r:=(diameter/2.0))
    gc += elt
  }


  def fillPolygon(gc: Context)(transform: Transform, fill: Fill, points: Array[Point]): Unit = {
    val dAttr = Svg.toSvgPath(points, Svg.Closed)
    val style = Svg.toStyle(fill)
    val elt = svg.path(svgAttrs.transform:=Svg.toSvgTransform(transform), svgAttrs.style:=style, svgAttrs.d:=dAttr)
    gc += elt
  }
  def strokePolygon(gc: Context)(transform: Transform, stroke: Stroke, points: Array[Point]): Unit = {
    val dAttr = Svg.toSvgPath(points, Svg.Closed)
    val style = Svg.toStyle(stroke)
    val elt = svg.path(svgAttrs.transform:=Svg.toSvgTransform(transform), svgAttrs.style:=style, svgAttrs.d:=dAttr)
    gc += elt
  }

  def fillClosedPath(gc: Context)(transform: Transform,
                            fill: Fill,
                            elements: List[PathElement]): Unit = {
    val dAttr = Svg.toSvgPath(elements, Svg.Closed)
    val style = Svg.toStyle(fill)
    val elt = svg.path(svgAttrs.transform:=Svg.toSvgTransform(transform), svgAttrs.style:=style, svgAttrs.d:=dAttr)
    gc += elt
  }
  def strokeClosedPath(gc: Context)(transform: Transform,
                              stroke: Stroke,
                              elements: List[PathElement]): Unit = {
    val dAttr = Svg.toSvgPath(elements, Svg.Closed)
    val style = Svg.toStyle(stroke)
    val elt = svg.path(svgAttrs.transform:=Svg.toSvgTransform(transform), svgAttrs.style:=style, svgAttrs.d:=dAttr)
    gc += elt
  }

  def fillOpenPath(gc: Context)(transform: Transform,
                          fill: Fill,
                          elements: List[PathElement]): Unit = {
    val dAttr = Svg.toSvgPath(elements, Svg.Open)
    val style = Svg.toStyle(fill)
    val elt = svg.path(svgAttrs.transform:=Svg.toSvgTransform(transform), svgAttrs.style:=style, svgAttrs.d:=dAttr)
    gc += elt
  }
  def strokeOpenPath(gc: Context)(transform: Transform,
                            stroke: Stroke,
                            elements: List[PathElement]): Unit = {
    val dAttr = Svg.toSvgPath(elements, Svg.Open)
    val style = Svg.toStyle(stroke)
    val elt = svg.path(svgAttrs.transform:=Svg.toSvgTransform(transform), svgAttrs.style:=style, svgAttrs.d:=dAttr)
    gc += elt
  }
}
