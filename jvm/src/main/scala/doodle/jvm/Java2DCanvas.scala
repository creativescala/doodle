package doodle
package jvm

import doodle.core.{Color, Stroke => DoodleStroke}
import doodle.backend.Canvas

class Java2DCanvas(panel: CanvasPanel) extends Canvas {
  import CanvasPanel._

  val queue = panel.queue

  def queueAndRepaint(op: Op) = {
    queue.add(op)
    panel.repaint()
  }

  def setSize(width: Int, height: Int): Unit =
    queue.add(SetSize(width, height))
  def setOrigin(x: Int, y: Int): Unit =
    queue.add(SetOrigin(x, y))
  def clear(color: Color): Unit =
    queueAndRepaint(Clear(color))
  def beginPath(): Unit =
    queue.add(BeginPath())
  def bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, endX: Double, endY: Double): Unit =
    queue.add(BezierCurveTo(cp1x, cp1y, cp2x, cp2y, endX, endY))
  def endPath(): Unit =
    queue.add(EndPath())
  def fill(): Unit =
    queueAndRepaint(Fill())
  def lineTo(x: Double, y: Double): Unit =
    queue.add(LineTo(x, y))
  def moveTo(x: Double, y: Double): Unit =
    queue.add(MoveTo(x, y))
  def setFill(color: Color): Unit =
    queue.add(SetFill(color))
  def setStroke(stroke: DoodleStroke): Unit =
    queue.add(SetStroke(stroke))
  def stroke(): Unit =
    queueAndRepaint(Stroke())
  def setAnimationFrameCallback(callback: Double => Unit): Unit =
    queue.add(SetAnimationFrameCallback(callback))
}

object Java2DCanvas {
  implicit def canvas: Canvas = {
    val frame = new CanvasFrame()
    frame.setVisible(true)
    frame.panel.canvas
  }
}
