package doodle
package js

import doodle.core._
import doodle.core.transform.Transform
import doodle.backend._

import org.scalajs.dom

final case class SvgCanvas(canvas: dom.svg.SVG, width: Int, height: Int) extends Draw {
  def draw(interpreter: Configuration => Interpreter, image: Image): Unit = {
    import scalatags.JsDom.styles.{font => cssFont}
    import scalatags.JsDom.{svgTags => svg}
    import scalatags.JsDom.svgAttrs
    import scalatags.JsDom.implicits._

    val dc = DrawingContext.blackLines
    val metrics = FontMetrics(canvas).boundingBox _
    val renderable = interpreter(dc, metrics)(image)

    val screenCenter = Point.cartesian(width / 2, height / 2)
    val center = renderable.boundingBox.center

    // Convert from canvas coordinates to end coordinates
    val canvasToScreen: Transform =
      Transform.translate(-center.x, -center.y)
        .andThen(Transform.horizontalReflection)
        .andThen(Transform.translate(screenCenter.x, screenCenter.y))

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

    val root = svg.g(svgAttrs.transform:=toSvgTransform(canvasToScreen)).render
    canvas.appendChild(root)

    import CanvasElement._
    renderable.elements.foreach {
      case ClosedPath(ctx, elts) =>
        val dAttr = SvgCanvas.toSvgPath(elts) ++ "Z"
        val style = SvgCanvas.toStyle(ctx)
        val elt = svg.path(svgAttrs.style:=style, svgAttrs.d:=dAttr).render
        root.appendChild(elt)

      case OpenPath(ctx, elts) =>
        val dAttr = SvgCanvas.toSvgPath(elts)
        val style = SvgCanvas.toStyle(ctx)
        val elt = svg.path(svgAttrs.style:=style, svgAttrs.d:=dAttr).render
        root.appendChild(elt)

      case Text(ctx, tx, bb, chars) =>
        val font = ctx.font.foreach { f =>
          val style = SvgCanvas.toStyle(ctx)
          // SVG x and y coordinates give the bottom left corner of the text. Our
          // bounding box origin is at the center of the text.
          val bottomLeft = Transform.translate(-bb.width/2, -bb.height/2)
          val fullTx = Transform.horizontalReflection andThen tx andThen bottomLeft
          val font = FontMetrics.toCss(f)
          val elt = svg.text(svgAttrs.style:=style,
                             svgAttrs.x:=0,
                             svgAttrs.y:=0,
                             svgAttrs.transform:=toSvgTransform(fullTx),
                             cssFont:=font,
                             chars).render
          root.appendChild(elt)

        }

      case Empty =>
        // Do nothing
    }
  }
}
object SvgCanvas {
  def fromElementId(id: String, width: Int, height: Int): SvgCanvas = {
    val elt = dom.document.getElementById(id).asInstanceOf[dom.svg.SVG]
    SvgCanvas(elt, width, height)
  }

  def toStyle(dc: DrawingContext): String = {
    import scala.collection.mutable.StringBuilder
    val builder = new StringBuilder(64)

    def toHSLA(color: Color): String = {
      val (h, s, l, a) = (color.hue, color.saturation, color.lightness, color.alpha)
      s"hsla(${h.toDegrees}, ${s.toPercentage}, ${l.toPercentage}, ${a.get})"
    }

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
}
