package doodle
package svg
package effect

import doodle.core._
import doodle.algebra.generic.{BoundingBox, Fill, Reified, Stroke}
import scalatags.generic.Bundle

case class Svg[Builder, Output <: FragT, FragT](bundle: Bundle[Builder, Output, FragT])/*(implicit stringAsAttr: AttrValue[Builder, String], doubleAsAttr: AttrValue[Builder, Double])*/ {
  def render(boundingBox: BoundingBox, instructions: List[Reified]) = {
    import bundle.{svgTags => svg}
    import bundle.svgAttrs
    import bundle.implicits._

    svg.svg(svgAttrs.width:=boundingBox.width, svgAttrs.height:=boundingBox.height)(
      instructions.map(reifiedToSvg(_)):_*
    )
  }

  def reifiedToSvg(reified: Reified) = {
    import Reified._
    import bundle.implicits._
    import bundle.{svgTags => svg}
    import bundle.svgAttrs

    reified match {
      case FillOpenPath(tx, fill, elements) =>
        val dAttr = Svg.toSvgPath(elements, Svg.Open)
        val style = Svg.toStyle(fill)
        svg.path(svgAttrs.transform:=Svg.toSvgTransform(tx), svgAttrs.style:=style, svgAttrs.d:=dAttr)

      case StrokeOpenPath(tx, stroke, elements) =>
        val dAttr = Svg.toSvgPath(elements, Svg.Open)
        val style = Svg.toStyle(stroke)
        svg.path(svgAttrs.transform:=Svg.toSvgTransform(tx), svgAttrs.style:=style, svgAttrs.d:=dAttr)


      case FillClosedPath(tx, fill, elements) =>
        val dAttr = Svg.toSvgPath(elements, Svg.Closed)
        val style = Svg.toStyle(fill)
        svg.path(svgAttrs.transform:=Svg.toSvgTransform(tx), svgAttrs.style:=style, svgAttrs.d:=dAttr)

      case StrokeClosedPath(tx, stroke, elements) =>
        val dAttr = Svg.toSvgPath(elements, Svg.Closed)
        val style = Svg.toStyle(stroke)
        svg.path(svgAttrs.transform:=Svg.toSvgTransform(tx), svgAttrs.style:=style, svgAttrs.d:=dAttr)


      case FillCircle(tx, fill, diameter) =>
        val style = Svg.toStyle(fill)
        svg.circle(svgAttrs.transform:=Svg.toSvgTransform(tx),
                   svgAttrs.style:=style,
                   svgAttrs.r:=(diameter/2.0))

      case StrokeCircle(tx, stroke, diameter) =>
        val style = Svg.toStyle(stroke)
        svg.circle(svgAttrs.transform:=Svg.toSvgTransform(tx),
                   svgAttrs.style:=style,
                   svgAttrs.r:=(diameter/2.0))


      case FillRect(tx, fill, width, height) =>
        val style = Svg.toStyle(fill)
        svg.rect(svgAttrs.transform:=Svg.toSvgTransform(tx),
                 svgAttrs.style:=style,
                 svgAttrs.width:=width,
                 svgAttrs.height:=height)

      case StrokeRect(tx, stroke, width, height) =>
        val style = Svg.toStyle(stroke)
        svg.rect(svgAttrs.transform:=Svg.toSvgTransform(tx),
                 svgAttrs.style:=style,
                 svgAttrs.width:=width,
                 svgAttrs.height:=height)


      case FillPolygon(tx, fill, points) =>
        val dAttr = Svg.toSvgPath(points, Svg.Closed)
        val style = Svg.toStyle(fill)
        svg.path(svgAttrs.transform:=Svg.toSvgTransform(tx), svgAttrs.style:=style, svgAttrs.d:=dAttr)

      case StrokePolygon(tx, stroke, points) =>
        val dAttr = Svg.toSvgPath(points, Svg.Closed)
        val style = Svg.toStyle(stroke)
        svg.path(svgAttrs.transform:=Svg.toSvgTransform(tx), svgAttrs.style:=style, svgAttrs.d:=dAttr)
    }
  }
}
object Svg {
  def toStyle(stroke: Stroke): String = {
    val builder = new StringBuilder(64)

    val linecap = stroke.cap match {
      case Cap.Butt => "butt"
      case Cap.Round => "round"
      case Cap.Square => "square"
    }
    val linejoin = stroke.join match {
      case Join.Bevel => "bevel"
      case Join.Round => "round"
      case Join.Miter => "miter"
    }
    builder ++= s"stroke-width: ${stroke.width}px; "
    builder ++= s"stroke: ${toHSLA(stroke.color)};"
    builder ++= s"stroke-linecap: ${linecap}; "
    builder ++= s"stroke-linejoin: ${linejoin}; "

    builder.toString
  }

  def toStyle(fill: Fill): String = {
    s"fill: ${toHSLA(fill.color)}"
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

  sealed trait PathType
  case object Open extends PathType
  case object Closed extends PathType

  def toSvgPath(elts: List[PathElement], pathType: PathType): String = {
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
    pathType match {
      case Open => builder.toString
      case Closed => (builder += 'Z').toString
    }
  }

  def toSvgPath(points: Array[Point], pathType: PathType): String = {
    import scala.collection.mutable.StringBuilder

    val builder = new StringBuilder(points.size * 10)
    points.foreach { pt =>
      builder ++= s"L ${pt.x} ${pt.y}"
    }

    pathType match {
      case Open => builder.toString
      case Closed => (builder += 'Z').toString
    }
  }


  def toHSLA(color: Color): String = {
    val (h, s, l, a) = (color.hue, color.saturation, color.lightness, color.alpha)
    s"hsla(${h.toDegrees}, ${s.get*100}%, ${l.get*100}%, ${a.get})"
  }

  def toRGB(color: Color): String = {
    val (r, g, b) = (color.red, color.green, color.blue)
    s"rgb(${r.get}, ${g.get}, ${b.get})"
  }
}
