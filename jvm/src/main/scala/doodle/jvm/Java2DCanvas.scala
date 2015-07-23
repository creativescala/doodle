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

  def beginPath(): Unit =
    queueAndRepaint(BeginPath())
  def bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, endX: Double, endY: Double): Unit =
    queueAndRepaint(BezierCurveTo(cp1x, cp1y, cp2x, cp2y, endX, endY))
  def endPath(): Unit =
    queueAndRepaint(EndPath())
  def fill(): Unit =
    queueAndRepaint(Fill())
  def lineTo(x: Double, y: Double): Unit =
    queueAndRepaint(LineTo(x, y))
  def moveTo(x: Double, y: Double): Unit =
    queueAndRepaint(MoveTo(x, y))
  def setFill(color: Color): Unit =
    queueAndRepaint(SetFill(color))
  def setStroke(stroke: DoodleStroke): Unit =
    queueAndRepaint(SetStroke(stroke))
  def stroke(): Unit =
    queueAndRepaint(Stroke())
}
