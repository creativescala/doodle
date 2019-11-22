/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package java2d
package algebra

import doodle.core.{
  Cap,
  Color,
  Gradient,
  Join,
  PathElement,
  Point,
  Transform => Tx
}
import doodle.core.font._
import doodle.algebra.generic.{BoundingBox, DrawingContext, Stroke, Fill}
import java.awt.{
  Color => AwtColor,
  BasicStroke,
  Font => AwtFont,
  FontMetrics,
  Graphics2D,
  LinearGradientPaint,
  Paint,
  RadialGradientPaint
}
import java.awt.MultipleGradientPaint.CycleMethod
import java.awt.geom.{AffineTransform, Path2D, Point2D, Rectangle2D}

/** Various utilities for using Java2D */
object Java2D {
  def fontMetrics(gc: Graphics2D, font: Font): FontMetrics =
    gc.getFontMetrics(Java2D.toAwtFont(font))

  def textBoundingBox(gc: Graphics2D, text: String, font: Font): BoundingBox = {
    val bounds = textBounds(gc, text, font)

    BoundingBox.centered(bounds.getWidth(), bounds.getHeight())
  }

  def textBounds(gc: Graphics2D, text: String, font: Font): Rectangle2D = {
    val metrics = fontMetrics(gc, font)
    metrics.getStringBounds(text, gc)
  }

  def toAwtFont(font: Font): AwtFont = {
    val awtFamily =
      font.family match {
        case FontFamily.Serif      => AwtFont.SERIF
        case FontFamily.SansSerif  => AwtFont.SANS_SERIF
        case FontFamily.Monospaced => AwtFont.MONOSPACED
        case FontFamily.Named(name)           => name
      }

    val awtStyle =
      font.face match {
        case FontFace.Bold   => AwtFont.BOLD
        case FontFace.Italic => AwtFont.ITALIC
        case FontFace.Normal => AwtFont.PLAIN
      }

    val awtSize =
      font.size match {
        case FontSize.Points(pts) => pts
      }

    new AwtFont(awtFamily, awtStyle, awtSize)
  }

  def toPoint2D(point: Point): Point2D =
    new Point2D.Double(point.x, point.y)

  def toAwtColor(color: Color): AwtColor = {
    val rgba = color.toRGBA
    new AwtColor(rgba.r.get, rgba.g.get, rgba.b.get, rgba.a.toUnsignedByte.get)
  }

  def setStroke(graphics: Graphics2D, stroke: Stroke) = {
    val width = stroke.width.toFloat
    val cap = stroke.cap match {
      case Cap.Butt   => BasicStroke.CAP_BUTT
      case Cap.Round  => BasicStroke.CAP_ROUND
      case Cap.Square => BasicStroke.CAP_SQUARE
    }
    val join = stroke.join match {
      case Join.Bevel => BasicStroke.JOIN_BEVEL
      case Join.Miter => BasicStroke.JOIN_MITER
      case Join.Round => BasicStroke.JOIN_ROUND
    }

    val jStroke =
      stroke.dash match {
        case None =>
          new BasicStroke(width, cap, join)

        case Some(dash) =>
          new BasicStroke(width, cap, join, 1.0f, dash, 0.0f)
      }
    val jColor = Java2D.toAwtColor(stroke.color)

    graphics.setStroke(jStroke)
    graphics.setColor(jColor)
  }

  def setFill(graphics: Graphics2D, fill: Fill) = {
    val paint: Paint =
      fill match {
        case Fill.ColorFill(c) =>
          Java2D.toAwtColor(c)

        case Fill.GradientFill(g) =>
          g match {
            case l: Gradient.Linear =>
              Java2D.toLinearGradientPaint(l)
            case r: Gradient.Radial =>
              Java2D.toRadialGradientPaint(r)
          }
      }
    graphics.setPaint(paint)
  }

  def toCycleMethod(cycleMethod: Gradient.CycleMethod): CycleMethod =
    cycleMethod match {
      case Gradient.CycleMethod.NoCycle => CycleMethod.NO_CYCLE
      case Gradient.CycleMethod.Reflect => CycleMethod.REFLECT
      case Gradient.CycleMethod.Repeat  => CycleMethod.REPEAT
    }

  def toLinearGradientPaint(gradient: Gradient.Linear): LinearGradientPaint = {
    val start = this.toPoint2D(gradient.start)
    val end = this.toPoint2D(gradient.end)
    val fractions = gradient.stops.map(_._2).map(_.toFloat).toArray
    val colors = gradient.stops.map(_._1).map(this.toAwtColor(_)).toArray
    val cycleMethod = this.toCycleMethod(gradient.cycleMethod)

    new LinearGradientPaint(start, end, fractions, colors, cycleMethod)
  }

  def toRadialGradientPaint(gradient: Gradient.Radial): RadialGradientPaint = {
    val center = this.toPoint2D(gradient.outer)
    val focus = this.toPoint2D(gradient.inner)
    val radius = gradient.radius.toFloat
    val fractions = gradient.stops.map(_._2).map(_.toFloat).toArray
    val colors = gradient.stops.map(_._1).map(this.toAwtColor(_)).toArray
    val cycleMethod = this.toCycleMethod(gradient.cycleMethod)

    new RadialGradientPaint(
      center,
      radius,
      focus,
      fractions,
      colors,
      cycleMethod
    )
  }

  /** Converts to an *open* `Path2D` */
  def toPath2D(elements: List[PathElement]): Path2D = {
    import PathElement._
    import Point.extractors._

    val path = new Path2D.Double()
    path.moveTo(0, 0)
    // path.moveTo(origin.x, origin.y)
    elements.foreach {
      case MoveTo(Cartesian(x, y)) =>
        path.moveTo(x, y)
      case LineTo(Cartesian(x, y)) =>
        path.lineTo(x, y)

      case BezierCurveTo(
          Cartesian(cp1x, cp1y),
          Cartesian(cp2x, cp2y),
          Cartesian(endX, endY)
          ) =>
        path.curveTo(
          cp1x,
          cp1y,
          cp2x,
          cp2y,
          endX,
          endY
        )
    }
    path
  }

  def toAffineTransform(transform: Tx): AffineTransform = {
    val elts = transform.elements
    new AffineTransform(elts(0), elts(3), elts(1), elts(4), elts(2), elts(5))
  }

  def withTransform(graphics: Graphics2D, transform: Tx)(f: => Unit): Unit = {
    val original = graphics.getTransform()
    graphics.transform(toAffineTransform(transform))
    f
    graphics.setTransform(original)
  }

  def strokeAndFill(
      graphics: Graphics2D,
      path: Path2D,
      current: DrawingContext
  ): Unit = {
    current.stroke.foreach { s =>
      setStroke(graphics, s)
      graphics.draw(path)
    }
    current.fill.foreach { f =>
      setFill(graphics, f)
      graphics.fill(path)
    }
  }

}
