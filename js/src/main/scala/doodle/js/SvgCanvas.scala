package doodle
package js

import doodle.core._
import doodle.backend._

import org.scalajs.dom

final case class SvgCanvas(root: dom.svg.SVG, width: Int, height: Int) extends Draw {
  def draw(interpreter: Configuration => Interpreter, image: Image): Unit = {
    import scalatags.JsDom.implicits._
    import scalatags.JsDom.{svgTags => svg}
    import scalatags.JsDom.svgAttrs

    val dc = DrawingContext.blackLines
    val metrics = FontMetrics(root).boundingBox _
    val renderable = interpreter(dc, metrics)(image)

    val screenCenter = Point.cartesian(width / 2, height / 2)
    val center = renderable.boundingBox.center

    // Convert from canvas coordinates to screen coordinates
    def canvasToScreen(canvas: Point): Point = {
      val offsetX = screenCenter.x
      val offsetY = screenCenter.y
      val centerX = center.x
      val centerY = center.y
      Point.cartesian(canvas.x - centerX + offsetX, offsetY - canvas.y + centerY)
    }

    import CanvasElement._
    renderable.elements.foreach {
      case ClosedPath(ctx, at, elts) =>
        val dAttr = SvgCanvas.pathToSvgPath(canvasToScreen _, elts) ++ "Z"
        val elt = svg.path(svgAttrs.d:=dAttr).render
        root.appendChild(elt)
      case OpenPath(ctx, at, elts) =>
        val dAttr = SvgCanvas.pathToSvgPath(canvasToScreen _, elts)
        val elt = svg.path(svgAttrs.d:=dAttr).render
        root.appendChild(elt)
      case Text(ctx, at, bb, chars) =>
        val elt = svg.text(chars).render
        root.appendChild(elt)
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

  def pathToSvgPath(transform: Point => Point, elts: List[PathElement]): String = {
    import scala.collection.mutable.StringBuilder
    val builder = new StringBuilder(64)
    builder ++= "M 0 0 "

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
