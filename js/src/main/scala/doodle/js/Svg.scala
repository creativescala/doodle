package doodle
package js

import doodle.core._
import doodle.core.transform.Transform

/** Utilities for working with SVG */
object Svg {
  def toHSLA(color: Color): String = {
    val (h, s, l, a) = (color.hue, color.saturation, color.lightness, color.alpha)
    s"hsla(${h.toDegrees}, ${s.toPercentage}, ${l.toPercentage}, ${a.get})"
  }

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
      case Fill(color) =>
        builder ++= s"fill: ${toHSLA(color)}; "
    }

    builder.toString
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
