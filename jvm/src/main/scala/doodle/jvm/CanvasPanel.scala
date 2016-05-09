package doodle
package jvm

import doodle.core.{DrawingContext, Color, Line, RGBA, Stroke, Point, Image, MoveTo, LineTo, BezierCurveTo, PathElement, Vec}
import doodle.backend.{Canvas, Configuration, Metrics, Interpreter, CanvasElement}

import java.util.concurrent.ConcurrentLinkedQueue
import java.awt.{Color => AwtColor, BasicStroke, Dimension, Graphics, Graphics2D, RenderingHints, Rectangle, Shape}
import java.awt.geom.Path2D
import java.awt.event.{ActionListener, ActionEvent}
import javax.swing.{JPanel, SwingUtilities}

import scala.collection.mutable.Queue

class CanvasPanel extends JPanel with Canvas {
  def draw(interpreter: Configuration => Interpreter, image: Image): Unit = {
    queue.add( (interpreter, image) )
    this.repaint()
  }

  // Drawing must be done on the Swing thread, while calls to draw may be done
  // by other threads. We solve this issue by having external users communicate with
  // the CanvasPanel via a concurrent queue.
  val queue = new ConcurrentLinkedQueue[(Configuration => Interpreter, Image)]()

  override def paintComponent(graphics: Graphics): Unit = {
    import CanvasElement._
    import Point.extractors.Cartesian

    val context = graphics.asInstanceOf[Graphics2D]
    context.setRenderingHints(
      new RenderingHints(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      )
    )

    // The origin in canvas coordinates
    var center = Point.cartesian(0, 0)

    // Convert from canvas coordinates to screen coordinates
    def canvasToScreen(x: Double, y: Double): Point = {
      val offsetX = getWidth / 2
      val offsetY = getHeight / 2
      val centerX = center.toCartesian.x
      val centerY = center.toCartesian.y
      Point.cartesian(x - centerX + offsetX, offsetY - y + centerY)
    }

    def stroke(path: Path2D, stroke: Stroke) = {
      val width = stroke.width.toFloat
      val cap = stroke.cap match {
        case Line.Cap.Butt => BasicStroke.CAP_BUTT
        case Line.Cap.Round => BasicStroke.CAP_ROUND
        case Line.Cap.Square => BasicStroke.CAP_SQUARE
      }
      val join = stroke.join match {
        case Line.Join.Bevel => BasicStroke.JOIN_BEVEL
        case Line.Join.Miter => BasicStroke.JOIN_MITER
        case Line.Join.Round => BasicStroke.JOIN_ROUND
      }
      val jStroke = new BasicStroke(width, cap, join)
      val jColor = awtColor(stroke.color)

      context.setStroke(jStroke)
      context.setPaint(jColor)

      context.draw(path)
    }

    def fill(path: Path2D, fill: Color) = {
      context.setPaint(awtColor(fill))
      context.fill(path)
    }

    def pathToPath2D(elts: List[PathElement], offsetX: Double, offsetY: Double): Path2D = {
      val path = new Path2D.Double()
      elts.foreach {
        case MoveTo(Cartesian(x, y)) =>
          val screen = canvasToScreen(x + offsetX, y + offsetY).toCartesian
          path.moveTo(screen.x, screen.y)
        case LineTo(Cartesian(x, y)) =>
          val screen = canvasToScreen(x + offsetX, y + offsetY).toCartesian
          path.lineTo(screen.x, screen.y)

        case BezierCurveTo(Cartesian(cp1x, cp1y), Cartesian(cp2x, cp2y), Cartesian(endX, endY)) =>
          val screenCp1 = canvasToScreen(cp1x + offsetX, cp1y + offsetY).toCartesian
          val screenCp2 = canvasToScreen(cp2x + offsetX, cp2y + offsetY).toCartesian
          val screenEnd = canvasToScreen(endX + offsetX, endY + offsetY).toCartesian
          path.curveTo(
            screenCp1.x , screenCp1.y,
            screenCp2.x , screenCp2.y,
            screenEnd.x , screenEnd.y
          )
      }
      path
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
            val path = pathToPath2D(elts, at.x, at.y)
            path.closePath()
            ctx.stroke.map(s => stroke(path, s))
            ctx.fillColor.map(c => fill(path, c))

          case OpenPath(ctx, at, elts) =>
            val path = pathToPath2D(elts, at.x, at.y)
            ctx.stroke.map(s => stroke(path, s))
            ctx.fillColor.map(c => fill(path, c))

          case Text(ctx, at, bb, chars) =>
            // drawString takes the bottom left corner of the text
            val bottomLeft = at - Vec(bb.width/2, bb.height/2)
            val screen = canvasToScreen(bottomLeft.x, bottomLeft.y)
            ctx.font map { f =>
              context.setFont(FontMetrics.toJFont(f))
              context.drawString(chars, screen.x.toInt, screen.y.toInt)
            }

          case Empty => // do nothing
        }
    }
  }

  // The (Configuration => Interpreter, Image) pairs we have pulled off the queue
  private val drawables = new Queue[(Configuration => Interpreter, Image)]()

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
