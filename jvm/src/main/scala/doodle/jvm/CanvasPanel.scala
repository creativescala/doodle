package doodle
package jvm

import doodle.core.{DrawingContext, Color, Line, RGBA, Stroke => DoodleStroke, Point, Image, MoveTo, LineTo, BezierCurveTo, PathElement, Vec}
import doodle.backend.{Metrics, Interpreter, OpenPath, ClosedPath, Text, Empty}

import java.util.concurrent.ConcurrentLinkedQueue
import java.awt.{Color => AwtColor, BasicStroke, Dimension, Graphics, Graphics2D, RenderingHints, Rectangle, Shape}
import java.awt.geom.Path2D
import java.awt.event.{ActionListener, ActionEvent}
import javax.swing.{JPanel, SwingUtilities, Timer}

import scala.collection.mutable.Queue

class CanvasPanel extends JPanel {
  def draw(interpreter: (DrawingContext, Metrics) => Interpreter, image: Image): Unit = {
    println("Pushing")
    queue.add( (interpreter, image) )
    println("Requesting repaint")
    this.repaint()
  }

  // Drawing must be done on the Swing thread, while calls to draw may be done
  // by other threads. We solve this issue by having the canvas communicate with
  // the panel via a concurrent queue.
  val queue = new ConcurrentLinkedQueue[((DrawingContext, Metrics) => Interpreter, Image)]()

  var currentTimer: Timer = null

  override def paintComponent(graphics: Graphics): Unit = {
    import Point.extractors.Cartesian

    val context = graphics.asInstanceOf[Graphics2D]
    context.setRenderingHints(new RenderingHints(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                              ))

    // The origin in canvas coordinates
    var center = Point.cartesian(0, 0)
    // Convert from canvas coordinates to screen coordinates
    def canvasToScreen(x: Double, y: Double): Point = {
      val offsetX = getWidth / 2
      val offsetY = getHeight / 2
      //println(s"Converting ($x,$y) to screen")
      //println(s"Center $center")
      //println(s"Offset (${offsetX},${offsetY})")
      val centerX = center.toCartesian.x
      val centerY = center.toCartesian.y
      //println(s"(${x - centerX + offsetX}, ${offsetY - y + centerY})")
      Point.cartesian(x - centerX + offsetX, offsetY - y + centerY)
    }


    var currentStroke: DoodleStroke = null
    var currentFill: Color = null
    var currentPath: Path2D = null

    def setContext(dc: DrawingContext): Unit = {
      dc.fillColor.foreach(fc => currentFill = fc)
      dc.stroke.foreach(s => currentStroke = s)
      dc.font.foreach(f => context.setFont(FontMetrics.toJFont(f)))
    }

    def stroke() = {
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
    }

    def fill() = {
      if(currentFill != null && currentPath != null) {
        context.setPaint(awtColor(currentFill))
        context.fill(currentPath)
      }
    }

    def drawPath(elts: List[PathElement], offsetX: Double, offsetY: Double) =
      elts.foreach {
        case MoveTo(Cartesian(x, y)) =>
          val screen = canvasToScreen(x + offsetX, y + offsetY).toCartesian
          currentPath.moveTo(screen.x, screen.y)
        case LineTo(Cartesian(x, y)) =>
          val screen = canvasToScreen(x + offsetX, y + offsetY).toCartesian
          currentPath.lineTo(screen.x, screen.y)

        case BezierCurveTo(Cartesian(cp1x, cp1y), Cartesian(cp2x, cp2y), Cartesian(endX, endY)) =>
          val screenCp1 = canvasToScreen(cp1x + offsetX, cp1y + offsetY).toCartesian
          val screenCp2 = canvasToScreen(cp2x + offsetX, cp2y + offsetY).toCartesian
          val screenEnd = canvasToScreen(endX + offsetX, endY + offsetY).toCartesian
          currentPath.curveTo(
            screenCp1.x , screenCp1.y,
            screenCp2.x , screenCp2.y,
            screenEnd.x , screenEnd.y
          )
      }


    retrieveDrawables()
    drawables.foreach {
      case (interpreter, image) =>
        val metrics = FontMetrics(context.getFontRenderContext()).boundingBox _
        val dc = DrawingContext.blackLines
        val renderable = interpreter(dc, metrics)(image)

        val bb = renderable.boundingBox
        center = bb.center

        setPreferredSize(new Dimension(bb.width.toInt + 40, bb.height.toInt + 40))
        SwingUtilities.windowForComponent(this).pack()

        renderable.elements.foreach {
          case ClosedPath(ctx, at, elts) =>
            setContext(ctx)
            currentPath = new Path2D.Double()
            drawPath(elts, at.x, at.y)
            currentPath.closePath()
            stroke()
            fill()

          case OpenPath(ctx, at, elts) =>
            setContext(ctx)
            currentPath = new Path2D.Double()
            drawPath(elts, at.x, at.y)
            stroke()
            fill()

          case Text(ctx, at, bb, chars) =>
            setContext(ctx)
            // drawString takes the bottom left corner of the text
            val bottomLeft = at - Vec(bb.width/2, bb.height/2)
            val screen = canvasToScreen(bottomLeft.x, bottomLeft.y)
            context.drawString(chars, screen.x.toInt, screen.y.toInt)

          case Empty => // do nothing
        }
    }
  }
  // The ((DrawingContext, Metrics) => Interpreter, Image) pairs we have pulled off the queue
  private val drawables = new Queue[((DrawingContext, Metrics) => Interpreter, Image)]()

  private def retrieveDrawables(): Unit = {
    var drawable = queue.poll()
    while(drawable != null) {
      drawables += drawable
      drawable = queue.poll()
    }
  }

  private def awtColor(color: Color): AwtColor = {
    val RGBA(r, g, b, a) = color.toRGBA
    new AwtColor(r.get, g.get, b.get, a.toUnsignedByte.get)
  }
}
