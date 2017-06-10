package doodle
package js

import doodle.core._
import doodle.core.transform.Transform
import org.scalajs.dom
import org.scalajs.dom.svg.Defs

/** Utilities for working with SVG */
object Svg {
  import scalatags.JsDom.{svgTags => svg}
  import scalatags.JsDom.svgAttrs
  import scalatags.JsDom.implicits._
  import scalatags.JsDom.all.attr

  def addGradientToSvg(gradient: Gradient, defs: Defs) = {
    val queryResult = defs.querySelector(s"#${this.toGradientId(gradient)}")
    if (queryResult == null)
      defs.appendChild(this.toSvgGradient(gradient))
  }

  def toHSLA(color: Color): String = {
    val (h, s, l, a) = (color.hue, color.saturation, color.lightness, color.alpha)
    s"hsla(${h.toDegrees}, ${s.toPercentage}, ${l.toPercentage}, ${a.get})"
  }

  def toRGB(color: Color): String = {
    val (r, g, b) = (color.red, color.green, color.blue)
    s"rgb(${r.get}, ${g.get}, ${b.get})"
  }

  def toGradientId(gradient: Gradient): String = s"grad_${gradient.hashCode()}"

  def toStyle(dc: DrawingContext): String = {
    import scala.collection.mutable.StringBuilder
    val builder = new StringBuilder(64)

    dc.stroke.fold(builder ++= "stroke: none; ") {
      case Stroke(width, color, cap, join) =>
        val linecap = cap match {
          case Line.Cap.Butt => "butt"
          case Line.Cap.Round => "round"
          case Line.Cap.Square => "square"
        }
        val linejoin = join match {
          case Line.Join.Bevel => "bevel"
          case Line.Join.Round => "round"
          case Line.Join.Miter => "miter"
        }
        builder ++= s"stroke-width: ${width}px; "
        builder ++= s"stroke: ${toHSLA(color)};"
        builder ++= s"stroke-linecap: ${linecap}; "
        builder ++= s"stroke-linejoin: ${linejoin}; "
    }

    dc.fill.fold(builder ++= "fill: none; ") {
      case Fill.Color(color) =>
        builder ++= s"fill: ${toHSLA(color)}; "
      case Fill.Gradient(gradient) =>
        builder ++= s"fill: url(#${toGradientId(gradient)}); "
    }

    builder.toString
  }

  def toSvgSpreadMethod(cycleMethod: Gradient.CycleMethod): String = cycleMethod match {
    case _: Gradient.CycleMethod.NoCycle => "pad"
    case _: Gradient.CycleMethod.Reflect => "reflect"
    case _: Gradient.CycleMethod.Repeat => "repeat"
  }

  def toSvgGradientStop(tuple: (Color, Double)): dom.svg.Stop = {
    val offset = tuple._2
    val color = this.toRGB(tuple._1)
    val opacity = tuple._1.alpha.get
    svg.stop(svgAttrs.offset:=offset, svgAttrs.stopColor:=color, svgAttrs.stopOpacity:=opacity).render
  }

  def toSvgGradient(gradient: Gradient): dom.svg.Element = gradient match {
    case linear: Gradient.Linear => this.toSvgLinearGradient(linear)
    case radial: Gradient.Radial => this.toSvgRadialGradient(radial)
  }

  def toSvgLinearGradient(gradient: Gradient.Linear): dom.svg.LinearGradient = {
    val (x1, y1, x2, y2) = (gradient.start.x, gradient.start.y, gradient.end.x, gradient.end.y)
    val id = this.toGradientId(gradient)
    val spreadMethod = this.toSvgSpreadMethod(gradient.cycleMethod)
    val domGradient = svg.linearGradient(svgAttrs.id:=id, svgAttrs.x1:=x1, svgAttrs.y1:=y1, svgAttrs.x2:=x2,
      svgAttrs.y2:=y2, svgAttrs.spreadMethod:=spreadMethod, svgAttrs.gradientUnits:="userSpaceOnUse").render
    val stops = gradient.stops.map(this.toSvgGradientStop)
    stops.foreach(domGradient.appendChild(_))

    domGradient
  }

  def toSvgRadialGradient(gradient: Gradient.Radial): dom.svg.RadialGradient = {
    val (cx, cy, fx, fy, r) = (gradient.outer.x, gradient.outer.y, gradient.inner.x, gradient.inner.y, gradient.radius)
    val id = this.toGradientId(gradient)
    val spreadMethod = this.toSvgSpreadMethod(gradient.cycleMethod)
    val domGradient = svg.radialGradient(svgAttrs.id:=id, svgAttrs.cx:=cx, svgAttrs.cy:=cy, attr("fx"):=fx,
      attr("fy"):=fy, svgAttrs.r:=r, svgAttrs.spreadMethod:=spreadMethod, svgAttrs.gradientUnits:="userSpaceOnUse").render
    val stops = gradient.stops.map(this.toSvgGradientStop)
    stops.foreach(domGradient.appendChild(_))

    domGradient
  }

  def toSvgPath(elts: List[PathElement]): String = {
    import PathElement._
    import scala.collection.mutable.StringBuilder

    val builder = new StringBuilder(64)
    elts.foreach {
      case MoveTo(end) =>
        builder ++= s"M ${end.x} ${end.y} "
      case LineTo(end) =>
        builder ++= s"L ${end.x} ${end.y} "
      case BezierCurveTo(cp1, cp2, end) =>
        builder ++= s"C ${cp1.x} ${cp1.y}, ${cp2.x} ${cp2.y}, ${end.x} ${end.y} "
    }

    builder.toString
  }

  def toSvgTransform(tx: Transform): String = {
    val elt = tx.elements
    val a = elt(0)
    val b = elt(3)
    val c = elt(1)
    val d = elt(4)
    val e = elt(2)
    val f = elt(5)
    s"matrix($a,$b,$c,$d,$e,$f)"
  }
}
