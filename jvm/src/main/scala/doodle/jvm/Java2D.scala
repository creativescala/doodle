package doodle
package jvm

import doodle.core._
import doodle.backend.{CanvasElement, Metrics, Renderable}

import java.awt.{Color => AwtColor, BasicStroke, Graphics2D, RenderingHints}
import java.awt.image.BufferedImage
import java.awt.geom.Path2D

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

    // Convert from canvas coordinates to screen coordinates
    def canvasToScreen(x: Double, y: Double): Point = {
      val offsetX = screenCenter.x
      val offsetY = screenCenter.y
      val centerX = center.toCartesian.x
      val centerY = center.toCartesian.y
      Point.cartesian(x - centerX + offsetX, offsetY - y + centerY)
    }

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

    def toPath2D(elts: List[PathElement], offsetX: Double, offsetY: Double): Path2D = {
      val path = new Path2D.Double()
      // Paths must start with a move or AWT raises an exception. Thus we always
      // move to the origin to start.
      val origin = canvasToScreen(offsetX, offsetY)
      path.moveTo(origin.x, origin.y)
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

    def strokeAndFill(path: Path2D, previous: DrawingContext, current: DrawingContext): Unit = {
      current.stroke.foreach { s =>
        if(previous.stroke != current.stroke)
          setStroke(s)
        graphics.draw(path)
      }
      current.fillColor.foreach { f =>
        if(previous.fillColor != current.fillColor)
          setFill(f)
        graphics.fill(path)
      }
    }

    initialContext.stroke.foreach { s => setStroke(s) }
    initialContext.fillColor.foreach { f => setFill(f) }
    renderable.elements.foldLeft(initialContext) { (previousCtx, elt) =>
      elt match {
        case ClosedPath(ctx, at, elts) =>
          val path = toPath2D(elts, at.x, at.y)
          path.closePath()
          strokeAndFill(path, previousCtx, ctx)
          ctx

        case OpenPath(ctx, at, elts) =>
          val path = toPath2D(elts, at.x, at.y)
          strokeAndFill(path, previousCtx, ctx)
          ctx

        case Text(ctx, at, bb, chars) =>
          // drawString takes the bottom left corner of the text
          val bottomLeft = at - Vec(bb.width/2, bb.height/2)
          val screen = canvasToScreen(bottomLeft.x, bottomLeft.y)
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
            graphics.drawString(chars, screen.x.toInt, screen.y.toInt)
          }
          ctx

        case Empty =>
          previousCtx
      }
    }
  }
}
