package doodle
package jvm

import doodle.core._
import doodle.core.transform.Transform
import doodle.backend.{CanvasElement, Metrics, Renderable}

import java.awt.{Color => AwtColor, BasicStroke, Graphics2D, RenderingHints}
import java.awt.image.BufferedImage
import java.awt.geom.{AffineTransform, Path2D}

import scala.annotation.tailrec

/** Various utilities for using Java2D */
object Java2D {
  def setup(graphics: Graphics2D): Graphics2D = {
    graphics.setRenderingHints(
      new RenderingHints(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON
      )
    )

    graphics
  }

  def fontMetrics(graphics: Graphics2D): Metrics =
    FontMetrics(graphics.getFontRenderContext()).boundingBox _

  val bufferFontMetrics: Metrics = {
    val buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    val graphics = this.setup(buffer.createGraphics())

    fontMetrics(graphics)
  }

  def toAwtColor(color: Color): AwtColor = {
    val RGBA(r, g, b, a) = color.toRGBA
    new AwtColor(r.get, g.get, b.get, a.toUnsignedByte.get)
  }

  def draw(graphics: Graphics2D, screenCenter: Point, initialContext: DrawingContext, renderable: Renderable): Unit = {
    import CanvasElement._
    import Point.extractors.Cartesian

    val bb = renderable.boundingBox
    val center = bb.center

    // Convert from canvas coordinates to screen coordinates.
    //
    // Shift the center of the bounding box to the origin.
    // Reflect around the Y axis as the canvas Y coordinate is reversed compared
    // to the Java2D Y axis.
    // Then recenter the canvas to the center of the screen.
    val canvasToScreen: Transform =
      Transform.translate(-center.x, -center.y)
        .andThen(Transform.horizontalReflection)
        .andThen(Transform.translate(screenCenter.x, screenCenter.y))

    def setStroke(stroke: Stroke) = {
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
      val jColor = this.toAwtColor(stroke.color)

      graphics.setStroke(jStroke)
      graphics.setPaint(jColor)
    }

    def setFill(fill: Color) = {
      graphics.setPaint(this.toAwtColor(fill))
    }

    def toPath2D(elts: List[PathElement]): Path2D = {
      import PathElement._

      val path = new Path2D.Double()
      path.moveTo(0, 0)
      elts.foreach {
        case MoveTo(Cartesian(x, y)) =>
          path.moveTo(x, y)
        case LineTo(Cartesian(x, y)) =>
          path.lineTo(x, y)

        case BezierCurveTo(Cartesian(cp1x, cp1y), Cartesian(cp2x, cp2y), Cartesian(endX, endY)) =>
          path.curveTo(
            cp1x , cp1y,
            cp2x , cp2y,
            endX , endY
          )
      }
      path
    }

    def toAffineTransform(transform: Transform): AffineTransform = {
      val elts = transform.elements
      new AffineTransform(elts(0), elts(3), elts(1), elts(4), elts(2), elts(5))
    }

    def strokeAndFill(path: Path2D, previous: DrawingContext, current: DrawingContext): Unit = {
      current.stroke.foreach { s =>
        //if(previous.stroke != current.stroke)
          setStroke(s)
        graphics.draw(path)
      }
      current.fillColor.foreach { f =>
        //if(previous.fillColor != current.fillColor)
          setFill(f)
        graphics.fill(path)
      }
    }

    graphics.transform(toAffineTransform(canvasToScreen))
    initialContext.stroke.foreach { s => setStroke(s) }
    initialContext.fillColor.foreach { f => setFill(f) }

    @tailrec
    def loop(previousCtx: DrawingContext, elts: List[CanvasElement]): Unit = {
      elts match {
        case Nil => ()
        case e :: es =>
          e match {
            case ClosedPath(ctx, elts) =>
              val path = toPath2D(elts)
              path.closePath()
              strokeAndFill(path, previousCtx, ctx)
              loop(ctx, es)

            case OpenPath(ctx, elts) =>
              val path = toPath2D(elts)
              strokeAndFill(path, previousCtx, ctx)
              loop(ctx, es)

            case Text(ctx, tx, bb, chars) =>
              // We have to do a few different transformations here:
              //
              // - The canvas Y coordinate is reversed with respect to the
              // screen Y coordinate, so normally we have to reverse
              // coordinates. However `drawString` will draw text oriented
              // correctly on the screen with need to reverse our reverse.
              //
              // - `drawString` draws from the bottom left corner of the text
              // while the origin of the bounding box is the center of the text.
              // Thus we need to translate to the bottom left corner.
              val bottomLeft = Transform.translate(-bb.width/2, -bb.height/2)
              val fullTx = Transform.horizontalReflection andThen tx andThen bottomLeft
              val currentTx = graphics.getTransform()
              graphics.transform(toAffineTransform(fullTx))
              ctx.stroke.foreach { s =>
                if(previousCtx.stroke != ctx.stroke)
                  setStroke(s)
              }
              ctx.fillColor.foreach { f =>
                if(previousCtx.fillColor != ctx.fillColor)
                  setFill(f)
              }
              ctx.font map { f =>
                graphics.setFont(FontMetrics.toJFont(f))
                graphics.drawString(chars, 0, 0)
              }
              graphics.setTransform(currentTx)

              loop(ctx, es)

            case Empty =>
              loop(previousCtx, es)
          }
      }
    }

    loop(initialContext, renderable.elements)
  }
}
