package doodle
package jvm

import doodle.core.{Color, Line, RGBA, Stroke => DoodleStroke, Vec}
import doodle.backend.Canvas

import java.util.concurrent.ConcurrentLinkedQueue
import java.awt.{Color => AwtColor, BasicStroke, Dimension, Graphics, Graphics2D, RenderingHints, Rectangle, Shape}
import java.awt.geom.Path2D
import java.awt.event.{ActionListener, ActionEvent}
import javax.swing.{JPanel, SwingUtilities, Timer}

import scala.collection.mutable.Queue

class CanvasPanel extends JPanel {
  import CanvasPanel._

  // Drawing must be done on the Swing thread, while calls to the Canvas
  // attached to this panel may be done by other threads. We solve this issue by
  // having the canvas communicate with the panel via a concurrent queue.
  val queue = new ConcurrentLinkedQueue[Op]()
  val canvas = new Java2DCanvas(this)

  var currentTimer: Timer = null

  override def paintComponent(graphics: Graphics): Unit = {
    val context = graphics.asInstanceOf[Graphics2D]
    context.setRenderingHints(new RenderingHints(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                              ))

    // The origin in canvas coordinates
    var center = Vec(0, 0)
    // Convert from canvas coordinates to screen coordinates
    def canvasToScreen(x: Double, y: Double): Vec = {
      val offsetX = getWidth / 2
      val offsetY = getHeight / 2
      //println(s"Converting ($x,$y) to screen")
      //println(s"Center $center")
      //println(s"Offset (${offsetX},${offsetY})")
      val Vec(centerX, centerY) = center
      //println(s"(${x - centerX + offsetX}, ${offsetY - y + centerY})")
      Vec(x - centerX + offsetX, offsetY - y + centerY)
    }


    var currentStroke: DoodleStroke = null
    var currentFill: Color = null
    var currentPath: Path2D = null

    retrieveOps()
    operations.foreach {
      case SetSize(width, height) =>
        //println(s"SetSize ${width},${height}")
        setPreferredSize(new Dimension(width + 40, height + 40))
        SwingUtilities.windowForComponent(this).pack()

      case SetOrigin(x, y) =>
        center = Vec(x, y)

      case Clear(color) =>
        val oldColor = context.getColor()
        context.setColor(awtColor(color))
        context.fillRect(0, 0, getWidth, getHeight)
        context.setColor(oldColor)

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
        val Vec(screenX, screenY) = canvasToScreen(x, y)
        currentPath.moveTo(screenX, screenY)

      case LineTo(x, y) =>
        val Vec(screenX, screenY) = canvasToScreen(x, y)
        currentPath.lineTo(screenX, screenY)

      case BezierCurveTo(cp1x, cp1y, cp2x, cp2y, endX, endY) =>
        val Vec(screenCp1X, screenCp1Y) = canvasToScreen(cp1x, cp1y)
        val Vec(screenCp2X, screenCp2Y) = canvasToScreen(cp2x, cp2y)
        val Vec(screenEndX, screenEndY) = canvasToScreen(endX, endY)
        currentPath.curveTo(
          screenCp1X , screenCp1Y,
          screenCp2X , screenCp2Y,
          screenEndX , screenEndY
        )

      case EndPath() =>
        // Closing a path on the Java 2D canvas draws a line from the end point
        // to the starting point. We don't want to do this, as this stops people
        // drawing paths that aren't closed. Thus this is a no-op

      case SetAnimationFrameCallback(callback) =>
        if(currentTimer != null) {
          currentTimer.stop()
        }
        val MsPerFrame = 17 // 17 ms per frame at 60 fps
        val listener = new ActionListener() {
          def actionPerformed(evt: ActionEvent): Unit =
            callback()
        }
        currentTimer = new Timer(MsPerFrame, listener)
        currentTimer.setRepeats(true)
        currentTimer.start()
    }
  }
  // The Ops we have pulled off the queue
  private val operations = new Queue[Op]() 

  private def retrieveOps(): Unit = {
    var op = queue.poll()
    while(op != null) {
      op match {
        case Clear(_) =>
          // For efficiency, drop all preceding operations
          operations.clear()
        case _ =>
          ()
      }
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
  final case class SetSize(width: Int, height: Int) extends Op
  final case class Clear(color: Color) extends Op
  final case class SetStroke(stroke: DoodleStroke) extends Op
  final case class SetFill(color: Color) extends Op
  final case class Stroke() extends Op
  final case class Fill() extends Op
  final case class BeginPath() extends Op
  final case class MoveTo(x: Double, y: Double) extends Op
  final case class LineTo(x: Double, y: Double) extends Op
  final case class BezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, endX: Double, endY: Double) extends Op
  final case class EndPath() extends Op
  final case class SetAnimationFrameCallback(callbacl: () => Unit) extends Op
}
