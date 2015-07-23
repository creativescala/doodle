package doodle
package jvm

import doodle.core.{Color, Line, RGBA, Stroke => DoodleStroke, Vec}
import doodle.backend.Canvas

import java.util.concurrent.ConcurrentLinkedQueue
import java.awt.{Color => AwtColor, BasicStroke, Dimension, Graphics, Graphics2D, RenderingHints, Rectangle, Shape}
import java.awt.geom.Path2D
import javax.swing.{JPanel, SwingUtilities}

import scala.collection.mutable.Queue

class CanvasPanel extends JPanel {
  import CanvasPanel._

  // Drawing must be done on the Swing thread, while calls to the Canvas
  // attached to this panel may be done by other threads. We solve this issue by
  // having the canvas communicate with the panel via a concurrent queue.
  val queue = new ConcurrentLinkedQueue[Op]()
  val canvas = new Java2DCanvas(this)

  override def paintComponent(graphics: Graphics): Unit = {
    val context = graphics.asInstanceOf[Graphics2D]
    context.setRenderingHints(new RenderingHints(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                              ))
    var center = Vec(getWidth/2, getHeight/2)

    var currentStroke: DoodleStroke = null
    var currentFill: Color = null
    var currentPath: Path2D = null

    // True if we actually rendered something. used to avoid excessive resizing
    // of this panel
    var didRender: Boolean = false

    // Bounds on the rendered image so we can resize the panel to fit the image.
    var top = 0
    var left = 0
    var bottom = 0
    var right = 0

    def updateBounds(boundingBox: Rectangle): Unit = {
      top    = boundingBox.y min top
      left   = boundingBox.x min left
      right  = (boundingBox.x + boundingBox.width) max right
      bottom = (boundingBox.y + boundingBox.height) max bottom
    }

    retrieveOps()
    operations.foreach {
      case SetOrigin(x, y) =>
        center = Vec(getWidth/2, getHeight/2) + Vec(x, y)

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

          updateBounds(currentPath.getBounds())
          didRender = true
        }

      case Fill() =>
        if(currentFill != null && currentPath != null) {
          context.setPaint(awtColor(currentFill))
          context.fill(currentPath)

          updateBounds(currentPath.getBounds())
          didRender = true
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

    if(didRender) {
      setPreferredSize(new Dimension(right - left + 40, bottom - top + 40))
      SwingUtilities.windowForComponent(this).pack()
    }
  }
  // The Ops we have pulled off the queue
  private val operations = new Queue[Op]() 

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
  sealed trait Op
  final case class SetOrigin(x: Int, y: Int) extends Op
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
