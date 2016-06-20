package doodle
package js

import doodle.core._
import doodle.backend._

import org.scalajs.dom

final case class SvgCanvas(root: dom.svg.SVG, width: Int, height: Int) extends Draw {
  def draw(interpreter: Configuration => Interpreter, image: Image): Unit = {
    import scalatags.JsDom.styles.{font => cssFont}
    import scalatags.JsDom.{svgTags => svg}
    import scalatags.JsDom.svgAttrs
    import scalatags.JsDom.implicits._

    val dc = DrawingContext.blackLines
    val metrics = FontMetrics(root).boundingBox _
    val renderable = interpreter(dc, metrics)(image)

    val screenCenter = Point.cartesian(width / 2, height / 2)
    val center = renderable.boundingBox.center

    // Convert from canvas coordinates to screen coordinates
    def canvasToScreen(offset: Vec): Point => Point =
      (canvas: Point) => {
        Point.cartesian(
          canvas.x + offset.x - center.x + screenCenter.x,
          screenCenter.y - canvas.y - offset.y + center.y
        )
      }

    import CanvasElement._
    renderable.elements.foreach {
      case ClosedPath(ctx, at, elts) =>
        val dAttr = SvgCanvas.toSvgPath(canvasToScreen(at), elts) ++ "Z"
        val style = SvgCanvas.toStyle(ctx)
        val elt = svg.path(svgAttrs.style:=style, svgAttrs.d:=dAttr).render
        root.appendChild(elt)
      case OpenPath(ctx, at, elts) =>
        val dAttr = SvgCanvas.toSvgPath(canvasToScreen(at), elts)
        val style = SvgCanvas.toStyle(ctx)
        val elt = svg.path(svgAttrs.style:=style, svgAttrs.d:=dAttr).render
        root.appendChild(elt)
      case Text(ctx, at, bb, chars) =>
        val font = ctx.font.foreach { f =>
          val style = SvgCanvas.toStyle(ctx)
          // SVG x and y coordinates give the bottom left corner of the text. Our
          // bounding box origin is at the center of the text.
          val location = canvasToScreen(at)(Point.cartesian(bb.left, bb.bottom))
          val font = FontMetrics.toCss(f)
          val elt = svg.text(svgAttrs.style:=style,
                             svgAttrs.x:=location.x,
                             svgAttrs.y:=location.y,
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

  def toSvgPath(transform: Point => Point, elts: List[PathElement]): String = {
    import scala.collection.mutable.StringBuilder
    val builder = new StringBuilder(64)
    // Ensure the path starts with a move, by moving to the origin
    val origin = transform(Point.zero)
    builder ++= s"M ${origin.x} ${origin.y} "
    elts.foreach {
      case MoveTo(end) =>
        val screen = transform(end)
        builder ++= s"M ${screen.x} ${screen.y} "
      case LineTo(end) =>
        val screen = transform(end)
        builder ++= s"L ${screen.x} ${screen.y} "
      case BezierCurveTo(cp1, cp2, end) =>
        val screenCp1 = transform(cp1)
        val screenCp2 = transform(cp2)
        val screenEnd = transform(end)
        builder ++= s"C ${screenCp1.x} ${screenCp1.y}, ${screenCp2.x} ${screenCp2.y}, ${screenEnd.x} ${screenEnd.y} "
    }

    builder.toString
  }
}
