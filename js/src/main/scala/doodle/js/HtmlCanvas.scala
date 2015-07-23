package doodle
package js

import doodle.core.{Color, Stroke}
import doodle.backend.Canvas

import org.scalajs.dom.{CanvasRenderingContext2D, HTMLCanvasElement}

class HtmlCanvas(canvas: HTMLCanvasElement) extends Canvas {
  val context = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  val originX = canvas.width / 2
  val originY = canvas.height / 2

  /** Transform from Canvas coordinates to HTMLCanvasElement coordinates */
  def transformX(x: Double): Double =
    originX + x

  /** Transform from Canvas coordinates to HTMLCanvasElement coordinates */
  def transformY(y: Double): Double =
    originY - y

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
}
