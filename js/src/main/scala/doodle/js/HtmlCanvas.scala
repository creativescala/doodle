package doodle
package js

import doodle.core.{Color, Stroke}
import doodle.backend.{Canvas, Key}

import org.scalajs.dom

class HtmlCanvas(canvas: dom.raw.HTMLCanvasElement) extends Canvas {
  val context = canvas.getContext("2d").asInstanceOf[dom.raw.CanvasRenderingContext2D]
  var originX = canvas.width / 2
  var originY = canvas.height / 2
  var animationFrameCallbackHandle: Option[Int] = None

  /** Transform from Canvas coordinates to HTMLCanvasElement coordinates */
  def transformX(x: Double): Double =
    originX + x

  /** Transform from Canvas coordinates to HTMLCanvasElement coordinates */
  def transformY(y: Double): Double =
    originY - y

  def setOrigin(x: Int, y: Int): Unit = {
    originX = (canvas.width / 2) + x
    originY = (canvas.height / 2) + y
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

  def moveTo(x: Double, y: Double): Unit =
    context.moveTo(transformX(x), transformY(y))

  def lineTo(x: Double, y: Double): Unit =
    context.lineTo(transformX(x), transformY(y))

  def bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, endX: Double, endY: Double): Unit = {
    context.bezierCurveTo(
      transformX(cp1x), transformY(cp1y),
      transformX(cp2x), transformY(cp2y),
      transformX(endX), transformY(endY)
    )
  }

  def endPath(): Unit = 
    context.closePath()

  def setAnimationFrameCallback(callback: () => Unit): Unit = {
    animationFrameCallbackHandle.foreach(handle => dom.window.cancelAnimationFrame(handle))
    animationFrameCallbackHandle =
      Some(dom.window.requestAnimationFrame((_: Double)=> callback()))
  }

  def setKeyDownCallback(callback: Key => Unit): Unit =
    canvas.onkeydown = (evt: dom.raw.KeyboardEvent) => callback(KeyboardEvent.toKey(evt))
}

object HtmlCanvas {
  implicit def canvas(implicit elt: dom.raw.HTMLCanvasElement): Canvas =
    new HtmlCanvas(elt)

  def fromElementId(id: String): Canvas = {
    val elt = dom.document.getElementById(id).asInstanceOf[dom.raw.HTMLCanvasElement]
    canvas(elt)
  }
}
