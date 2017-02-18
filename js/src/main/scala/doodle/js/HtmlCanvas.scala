/*package doodle
package js

import doodle.core.{Color, Stroke, Vec}
import doodle.backend.Canvas

import org.scalajs.dom

class HtmlCanvas(canvas: dom.raw.HTMLCanvasElement) extends Canvas {
  val context = canvas.getContext("2d").asInstanceOf[dom.raw.CanvasRenderingContext2D]
  var animationFrameCallbackHandle: Option[Int] = None

  // The origin in canvas coordinates
  var center = Vec(0, 0)
  // Convert from canvas coordinates to screen coordinates
  def canvasToScreen(x: Double, y: Double): Vec = {
    val offsetX = canvas.width / 2
    val offsetY = canvas.height / 2
    val Vec(centerX, centerY) = center
    Vec(x - centerX + offsetX, offsetY - y + centerY)
  }

  def setOrigin(x: Int, y: Int): Unit = {
    center = Vec(x, y)
  }

  def clear(color: Color): Unit = {
    val oldFill = context.fillStyle
    context.fillStyle = color.toCanvas
    context.fillRect(0, 0, canvas.width, canvas.height)
    context.fillStyle = oldFill
  }

  def setSize(width: Int, height: Int): Unit = {
    canvas.width = width
    canvas.height = height
  }

  def setStroke(stroke: Stroke): Unit = {
    context.lineWidth = stroke.width
    context.lineCap = stroke.cap.toCanvas
    context.lineJoin = stroke.join.toCanvas
    context.strokeStyle = stroke.color.toCanvas
  }

  def stroke(): Unit =
    context.stroke()

  def setFill(color: Color): Unit = {
    context.fillStyle = color.toCanvas
  }

  def fill(): Unit =
    context.fill()

  def beginPath(): Unit =
    context.beginPath()

  def moveTo(x: Double, y: Double): Unit = {
    val Vec(screenX, screenY) = canvasToScreen(x, y)
    context.moveTo(screenX, screenY)
  }

  def lineTo(x: Double, y: Double): Unit = {
    val Vec(screenX, screenY) = canvasToScreen(x, y)
    context.lineTo(screenX, screenY)
  }

  def bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, endX: Double, endY: Double): Unit = {
    val Vec(screenCp1X, screenCp1Y) = canvasToScreen(cp1x, cp1y)
    val Vec(screenCp2X, screenCp2Y) = canvasToScreen(cp2x, cp2y)
    val Vec(screenEndX, screenEndY) = canvasToScreen(endX, endY)
    context.bezierCurveTo(
      screenCp1X , screenCp1Y,
      screenCp2X , screenCp2Y,
      screenEndX , screenEndY
    )
  }

  def endPath(): Unit =
    context.closePath()

  def setAnimationFrameCallback(callback: () => Unit): Unit = {
    animationFrameCallbackHandle.foreach(handle => dom.window.cancelAnimationFrame(handle))
    animationFrameCallbackHandle =
      Some(dom.window.requestAnimationFrame((_: Double)=> callback()))
  }
}

object HtmlCanvas {
  implicit def canvas(implicit elt: dom.raw.HTMLCanvasElement): Canvas =
    new HtmlCanvas(elt)

  def fromElementId(id: String): Canvas = {
    val elt = dom.document.getElementById(id).asInstanceOf[dom.raw.HTMLCanvasElement]
    canvas(elt)
  }
}
 */
