package doodle
package js

import doodle.core._
import doodle.core.font._
import doodle.backend._

import org.scalajs.dom

final case class SvgCanvas(root: dom.raw.SVGSVGElement) {
  def draw(interpreter: Configuration => Interpreter, image: Image): SvgCanvas = {
    import scalatags.JsDom.svgTags._
    import scalatags.JsDom.svgAttrs._

    val dc = DrawingContext.blackLines
    val metrics = FontMetrics(root).boundingBox _
    val renderable = interpreter(dc, metrics)(image)

    val center = renderable.boundingBox.center

    // Convert from canvas coordinates to screen coordinates
    def canvasToScreen(canvas: Point): Point = {
      val offsetX = screenCenter.x
      val offsetY = screenCenter.y
      val centerX = center.x
      val centerY = center.y
      Point.cartesian(canvas.x - centerX + offsetX, offsetY - canvas.y + centerY)
    }

    renderable.elements.foreach {
      case ClosedPath(ctx, at, elts) =>
        val dAttr = SvgCanvas.pathToSvgPath(canvasToScreen _, elts) ++ "Z"
        val elt = path(d:=dAttr).render
        svg.appendChild(elt)
      case OpenPath(ctx, at, elts) => ???
        val dAttr = SvgCanvas.pathToSvgPath(canvasToScreen _, elts)
        val elt = path(d:=dAttr).render
        svg.appendChild(elt)
      case Text(ctx, at, bb, chars) =>
        val elt = text(chars)
        svg.appendChild(elt)
      case Empty =>
        // Do nothing
    }
  }
}
object SvgCanvas {
  def pathToSvgPath(transform: Point => Point, elts: List[PathElement]): String = {
    import scala.collection.mutable.StringBuilder
    val builder = new StringBuilder(64)
    builder += "M 0 0 "

    elts.foreach {
      case MoveTo(end) =>
        val screen = transform(end)
        buider += s"M ${screen.x} ${screen.y} "
      case LineTo(end) =>
        val screen = transform(end)
        builder += s"L ${screen.x} ${screen.y} "
      case BezierCurveTo(cp1, cp2, end) =>
        val screenCp1 = transform(cp1)
        val screenCp2 = transform(cp2)
        val screenEnd = transform(end)
        builder += s"C ${screenCp1.x} ${screenCp1.y}, ${screenCp2.x} ${screenCp2.y}, ${screenEnd.x} ${screenEnd.y} "
    }
    builder.toString
  }
}
