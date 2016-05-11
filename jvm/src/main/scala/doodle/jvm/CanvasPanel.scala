package doodle
package jvm

import doodle.core.{DrawingContext, Color, Line, RGBA, Stroke, Point, Image, MoveTo, LineTo, BezierCurveTo, PathElement, Vec}
import doodle.backend.{BoundingBox, Canvas, Configuration, Metrics, Interpreter, CanvasElement}

import java.util.concurrent.ConcurrentLinkedQueue
import java.awt.{Color => AwtColor, BasicStroke, Dimension, Graphics, Graphics2D, RenderingHints, Rectangle, Shape}
import java.awt.geom.Path2D
import java.awt.event.{ActionListener, ActionEvent}
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.{JPanel, SwingUtilities}
import javax.imageio.ImageIO

import scala.collection.mutable.Queue

class CanvasPanel extends JPanel with Canvas[CanvasPanel] {
  def draw(interpreter: Configuration => Interpreter, image: Image): CanvasPanel = {
    queue.add( (interpreter, image) )
    this.repaint()
    this
  }

  def drawToBuffer(): BufferedImage = {
    retrieveDrawables()
    val bb = boundingBox(drawables.toList)
    val buffer = new BufferedImage(bb.width.ceil.toInt + 40, bb.height.ceil.toInt + 40, BufferedImage.TYPE_INT_ARGB)
    val graphics = buffer.createGraphics()
    val metrics = FontMetrics(graphics.getFontRenderContext()).boundingBox _
    val dc = DrawingContext.blackLines

    graphics.setRenderingHints(
      new RenderingHints(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      )
    )

    drawables.foreach {
      case (interpreter, image) =>
        val renderable = interpreter(dc, metrics)(image)
        val bb = renderable.boundingBox
        val screenCenter = Point.cartesian((bb.width+40) / 2, (bb.height+40) / 2)

        Java2DCanvas.drawToGraphics2D(graphics, screenCenter, renderable)
    }

    buffer
  }

  def saveToPng(fileName: String): Unit = {
    val buffer = drawToBuffer()
    val file = new File(fileName)
    ImageIO.write(buffer, "png", file);
  }

  /** Get a bounding box enclosing all the images we've been asked to draw. */
  def boundingBox(drawables: List[(Configuration => Interpreter, Image)]): BoundingBox = {
    // Used to get a Graphics2D from which we can construct font metrics.
    val dummy = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    val graphics = dummy.createGraphics()
    val metrics = FontMetrics(graphics.getFontRenderContext()).boundingBox _
    val dc = DrawingContext.blackLines

    val bbs = drawables.map { case (interpreter, image) =>
      val renderable = interpreter(dc, metrics)(image)
      renderable.boundingBox
    }
    bbs.foldLeft(BoundingBox.empty){ (accum, elt) =>
      accum on elt
    }
  }

  override def paintComponent(graphics: Graphics): Unit = {
    val context = graphics.asInstanceOf[Graphics2D]
    val metrics = FontMetrics(context.getFontRenderContext()).boundingBox _
    val dc = DrawingContext.blackLines

    context.setRenderingHints(
      new RenderingHints(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      )
    )

    retrieveDrawables()
    drawables.foreach {
      case (interpreter, image) =>
        val renderable = interpreter(dc, metrics)(image)
        val bb = renderable.boundingBox

        setPreferredSize(new Dimension(bb.width.toInt + 40, bb.height.toInt + 40))
        SwingUtilities.windowForComponent(this).pack()

        val screenCenter = Point.cartesian(getWidth / 2, getHeight / 2)

        Java2DCanvas.drawToGraphics2D(context, screenCenter, renderable)
    }
  }

  // Drawing must be done on the Swing thread, while calls to draw may be done
  // by other threads. We solve this issue by having external users communicate with
  // the CanvasPanel via a concurrent queue.
  private val queue = new ConcurrentLinkedQueue[(Configuration => Interpreter, Image)]()

  // The (Configuration => Interpreter, Image) pairs we have pulled off the queue
  private val drawables = new Queue[(Configuration => Interpreter, Image)]()

  private def retrieveDrawables(): Unit = {
    var drawable = queue.poll()
    while(drawable != null) {
      drawables += drawable
      drawable = queue.poll()
    }
  }

}
