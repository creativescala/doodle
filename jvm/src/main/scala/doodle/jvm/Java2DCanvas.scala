package doodle
package jvm

import doodle.core._
import doodle.backend.{Canvas, CanvasElement, Configuration, Interpreter, Renderable}
import java.awt.{Color => AwtColor, BasicStroke, Dimension, Graphics, Graphics2D, RenderingHints, Rectangle, Shape}
import java.awt.geom.Path2D

object Java2DCanvas {
  implicit def canvas: Canvas[CanvasPanel] = {
    val frame = new CanvasFrame()
    frame.setVisible(true)

    frame.panel
  }

  def drawToGraphics2D(graphics: Graphics2D, screenCenter: Point, renderable: Renderable): Unit = {
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
      val jColor = Java2DCanvas.awtColor(stroke.color)

      graphics.setStroke(jStroke)
      graphics.setPaint(jColor)

      graphics.draw(path)
    }

    def fill(path: Path2D, fill: Color) = {
      graphics.setPaint(Java2DCanvas.awtColor(fill))
      graphics.fill(path)
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
          graphics.setFont(FontMetrics.toJFont(f))
          graphics.drawString(chars, screen.x.toInt, screen.y.toInt)
        }

      case Empty => // do nothing
    }
  }

  def awtColor(color: Color): AwtColor = {
    val RGBA(r, g, b, a) = color.toRGBA
    new AwtColor(r.get, g.get, b.get, a.toUnsignedByte.get)
  }
}
