package doodle
package jvm

import doodle.core.{Color, Line, RGBA, Stroke => DoodleStroke, Vec}
import doodle.backend.Canvas

import java.util.concurrent.ConcurrentLinkedQueue
import java.awt.{Color => AwtColor, BasicStroke, Dimension, Graphics, Graphics2D, RenderingHints, Shape}
import java.awt.geom.Path2D
import javax.swing.JPanel

import scala.collection.mutable.Queue

class CanvasPanel extends JPanel {
  import CanvasPanel._

  // Drawing must be done on the Swing thread, while calls to the Canvas
  // attached to this panel may be done by other threads. We solve this issue by
  // having the canvas communicate with the panel via a concurrent queue.
  val queue = new ConcurrentLinkedQueue[Op]()
  val canvas = new Java2DCanvas(this)
  // The Ops we have pulled off the queue
  val operations = new Queue[Op]() 

  setPreferredSize(new Dimension(600, 600))

  override def paintComponent(graphics: Graphics): Unit = {
    retrieveOps()
    val context = graphics.asInstanceOf[Graphics2D]
    context.setRenderingHints(new RenderingHints(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                              ))
    val center = Vec(getWidth/2, getHeight/2)

    var currentStroke: DoodleStroke = null
    var currentFill: Color = null
    var currentPath: Path2D = null

    operations.foreach {
      case SetStroke(stroke) => 
        currentStroke = stroke
      case SetFill(color) =>
        currentFill = color
      case Stroke() =>
        if(currentStroke != null && currentPath != null) {
          val width = currentStroke.width.toFloat
          val cap = currentStroke.cap match {
            case Line.Cap.Butt => BasicStroke.CAP_BUTT
            case Line.Cap.Round => BasicStroke.CAP_ROUND
            case Line.Cap.Square => BasicStroke.CAP_SQUARE
          }
          val join = currentStroke.join match {
            case Line.Join.Bevel => BasicStroke.JOIN_BEVEL
            case Line.Join.Miter => BasicStroke.JOIN_MITER
            case Line.Join.Round => BasicStroke.JOIN_ROUND
          }
          val stroke = new BasicStroke(width, cap, join)
          val color = awtColor(currentStroke.color)

          context.setStroke(stroke)
          context.setPaint(color)

          context.draw(currentPath)
        } 
      case Fill() =>
        if(currentFill != null && currentPath != null) {
          context.setPaint(awtColor(currentFill))
          context.fill(currentPath)
        }
      case BeginPath() =>
        currentPath = new Path2D.Double()
      case MoveTo(x, y) =>
        currentPath.moveTo(center.x + x, center.y - y)
      case LineTo(x, y) =>
        currentPath.lineTo(center.x + x, center.y - y)
      case BezierCurveTo(cp1x, cp1y, cp2x, cp2y, endX, endY) =>
        currentPath.curveTo(
          center.x + cp1x , center.y - cp1y,
          center.x + cp2x , center.y - cp2y,
          center.x + endX , center.y - endY
        )
      case EndPath() =>
        currentPath.closePath()
    }
  }

  private def retrieveOps(): Unit = {
    var op = queue.poll()
    while(op != null) {
      operations += op
      op = queue.poll()
    }
  }

  private def awtColor(color: Color): AwtColor = {
    val RGBA(r, g, b, a) = color.toRGBA
    new AwtColor(r.get, g.get, b.get, a.toUnsignedByte.get)
  }
}

object CanvasPanel {
  implicit def canvas: Canvas = {
    val frame = new CanvasFrame()
    frame.setVisible(true)
    frame.panel.canvas
  }

  sealed trait Op
  final case class SetStroke(stroke: DoodleStroke) extends Op
  final case class SetFill(color: Color) extends Op
  final case class Stroke() extends Op
  final case class Fill() extends Op
  final case class BeginPath() extends Op
  final case class MoveTo(x: Double, y: Double) extends Op
  final case class LineTo(x: Double, y: Double) extends Op
  final case class BezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, endX: Double, endY: Double) extends Op
  final case class EndPath() extends Op
}
