/*
 * Copyright 2015 noelwelsh
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

import doodle.core.{Color,PathElement,Point}
import doodle.algebra.generic.{DrawingContext, Stroke, Fill}
import java.awt.{Color => AwtColor, BasicStroke, Graphics2D, RenderingHints}
// import java.awt.image.BufferedImage
// import java.awt.geom.{AffineTransform, Path2D}
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

  // def fontMetrics(graphics: Graphics2D): Metrics =
  //   FontMetrics(graphics.getFontRenderContext()).boundingBox _

  // val bufferFontMetrics: Metrics = {
  //   val buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
  //   val graphics = this.setup(buffer.createGraphics())

  //   fontMetrics(graphics)
  // }

  def toAwtColor(color: Color): AwtColor = {
    val rgba = color.toRGBA
    new AwtColor(rgba.r.get, rgba.g.get, rgba.b.get, rgba.a.toUnsignedByte.get)
  }

  def setStroke(graphics: Graphics2D, stroke: Stroke) = {
    val width = stroke.width.toFloat
    // val cap = stroke.cap match {
    //   case Line.Cap.Butt => BasicStroke.CAP_BUTT
    //   case Line.Cap.Round => BasicStroke.CAP_ROUND
    //   case Line.Cap.Square => BasicStroke.CAP_SQUARE
    // }
    // val join = stroke.join match {
    //   case Line.Join.Bevel => BasicStroke.JOIN_BEVEL
    //   case Line.Join.Miter => BasicStroke.JOIN_MITER
    //   case Line.Join.Round => BasicStroke.JOIN_ROUND
    // }
    val jStroke = new BasicStroke(width, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER)
    val jColor = Java2D.toAwtColor(stroke.color)

    graphics.setStroke(jStroke)
    graphics.setColor(jColor)
  }

  def setFill(graphics: Graphics2D, fill: Fill) = {
    graphics.setColor(Java2D.toAwtColor(fill.color))
  }

  /** Converts to an *open* `Path2D` */
  def toPath2D(elements: List[PathElement]): Path2D = {
    import PathElement._
    import Point.extractors._

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

  // def toAffineTransform(transform: Transform): AffineTransform = {
  //   val elts = transform.elements
  //   new AffineTransform(elts(0), elts(3), elts(1), elts(4), elts(2), elts(5))
  // }

  def strokeAndFill(graphics: Graphics2D,
                    path: Path2D,
                    current: DrawingContext): Unit = {
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
