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
package fx
package algebra

import doodle.algebra.generic._
import doodle.core._
import javafx.scene.effect.{BlendMode => FxBlendMode}
import javafx.scene.paint.{Color => FxColor}

object FxGraphicsContext extends GraphicsContext[javafx.scene.canvas.GraphicsContext] {
  type GC = javafx.scene.canvas.GraphicsContext

  def fillRect(gc: GC)(dc: DrawingContext, center: Point, width: Double, height: Double): Unit = {
    dc.fill.foreach{ _ =>
      setupDrawingContext(gc, dc)
      gc.fillRect(center.x - (width/2.0), center.y - (height/2.0), width, height)
    }
  }

  def strokeRect(gc: GC)(dc: DrawingContext, center: Point, width: Double, height: Double): Unit = {
    dc.stroke.foreach{ _ =>
      setupDrawingContext(gc, dc)
      gc.strokeRect(center.x - (width/2.0), center.y - (height/2.0), width, height)
    }
  }

  def fillCircle(gc: GC)(dc: DrawingContext, center: Point, radius: Double): Unit = {
    dc.fill.foreach{ _ =>
      setupDrawingContext(gc, dc)
      gc.fillOval(center.x - radius, center.y - radius, radius, radius)
    }
  }

  def strokeCircle(gc: GC)(dc: DrawingContext, center: Point, radius: Double): Unit = {
    dc.stroke.foreach{ _ =>
      setupDrawingContext(gc, dc)
      gc.strokeOval(center.x - radius, center.y - radius, radius, radius)
    }
  }

  def fillPolygon(gc: GC)(dc: DrawingContext, points: Array[Point]): Unit = {
    dc.fill.foreach{ _ =>
      val xs = points.map(_.x)
      val ys = points.map(_.y)
      gc.fillPolygon(xs, ys, points.length)
    }
  }

  def strokePolygon(gc: GC)(dc: DrawingContext, points: Array[Point]): Unit = {
    dc.stroke.foreach{ _ =>
      val xs = points.map(_.x)
      val ys = points.map(_.y)
      gc.strokePolygon(xs, ys, points.length)
    }
  }

  def fillClosedPath(gc: GC)(dc: DrawingContext, center: Point, elements: List[PathElement]): Unit = {
    dc.fill.foreach{ f =>
      setupDrawingContext(gc, dc)
      pathToFxPath(gc, center, elements)
      gc.closePath()
      gc.fill()
    }
  }
  def strokeClosedPath(gc: GC)(dc: DrawingContext, center: Point, elements: List[PathElement]): Unit = {
    dc.stroke.foreach{ s =>
      setupDrawingContext(gc, dc)
      pathToFxPath(gc, center, elements)
      gc.closePath()
      gc.stroke()
    }
  }

  def fillOpenPath(gc: GC)(dc: DrawingContext, center: Point, elements: List[PathElement]): Unit = {
    dc.fill.foreach{ f =>
      setupDrawingContext(gc, dc)
      pathToFxPath(gc, center, elements)
      gc.fill()
    }
  }
  def strokeOpenPath(gc: GC)(dc: DrawingContext, center: Point, elements: List[PathElement]): Unit = {
    dc.stroke.foreach{ s =>
      setupDrawingContext(gc, dc)
      pathToFxPath(gc, center, elements)
      gc.stroke()
    }
  }

  def setupDrawingContext(gc: GC, dc: DrawingContext): Unit = {
    import BlendMode._

    dc.blendMode.foreach{
      case Screen =>     gc.setGlobalBlendMode(FxBlendMode.SCREEN)
      case Burn =>       gc.setGlobalBlendMode(FxBlendMode.COLOR_BURN)
      case Dodge =>      gc.setGlobalBlendMode(FxBlendMode.COLOR_DODGE)
      case Lighten =>    gc.setGlobalBlendMode(FxBlendMode.LIGHTEN)
      case SourceOver => gc.setGlobalBlendMode(FxBlendMode.SRC_OVER)
    }
    dc.stroke.foreach{ stroke =>
      gc.setLineWidth(stroke.width)
      gc.setStroke(colorToFxColor(stroke.color))
    }
    dc.fill.foreach{ fill =>
      gc.setFill(colorToFxColor(fill.color))
    }
  }

  def pathToFxPath(gc: GC, origin: Point, path: List[PathElement]): Unit = {
    import PathElement._

    gc.beginPath()
    gc.moveTo(origin.x, origin.y)
    path.foreach{
      case MoveTo(pt) => gc.moveTo(pt.x, pt.y)
      case LineTo(pt) => gc.lineTo(pt.x, pt.y)
      case BezierCurveTo(cp1, cp2, end) =>
        gc.bezierCurveTo(cp1.x, cp1.y, cp2.x, cp2.y, end.x, end.y)
    }
  }

  def colorToFxColor(c: Color): FxColor = {
    val rgba = c.toRGBA

    FxColor.rgb(rgba.red.get, rgba.green.get, rgba.blue.get, rgba.alpha.get)
  }
}
