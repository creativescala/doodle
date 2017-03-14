package doodle
package jvm

import doodle.core._
import doodle.core.transform.Transform
import doodle.backend.Metrics

import java.awt.{Color => AwtColor, BasicStroke, Graphics2D, RenderingHints}
import java.awt.image.BufferedImage
import java.awt.geom.{AffineTransform, Path2D}

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

  def setStroke(graphics: Graphics2D, stroke: Stroke) = {
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

  def setFill(graphics: Graphics2D, fill: Color) = {
    graphics.setPaint(this.toAwtColor(fill))
  }

  /** Converts to an *open* `Path2D` */
  def toPath2D(elements: List[PathElement]): Path2D = {
    import PathElement._
    import Point.extractors.Cartesian

    val path = new Path2D.Double()
    path.moveTo(0, 0)
    elements.foreach {
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

  def strokeAndFill(graphics: Graphics2D,
                    path: Path2D,
                    current: DrawingContext): Unit = {
    current.stroke.foreach { s =>
      setStroke(graphics, s)
      graphics.draw(path)
    }
    current.fillColor.foreach { f =>
      setFill(graphics, f)
      graphics.fill(path)
    }
  }

}
