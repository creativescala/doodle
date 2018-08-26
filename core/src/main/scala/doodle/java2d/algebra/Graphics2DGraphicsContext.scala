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

import doodle.algebra.generic._
import doodle.core.{PathElement, Point}
import java.awt.Graphics2D

/** Higher level shape primitives */
object Graphics2DGraphicsContext extends GraphicsContext[Graphics2D] {
  def fillRect(gc: Graphics2D)(dc: DrawingContext, center: Point, width: Double, height: Double): Unit = {
    dc.fill.foreach{ f =>
      Java2D.setFill(gc, f)
      val w = width.toInt
      val h = width.toInt
      gc.fillRect(center.x.toInt - (w/2), center.y.toInt - (h/2), w, h)
    }
  }
  def strokeRect(gc: Graphics2D)(dc: DrawingContext, center: Point, width: Double, height: Double): Unit = {
    dc.stroke.foreach{ s =>
      Java2D.setStroke(gc, s)
      val w = width.toInt
      val h = width.toInt
      gc.drawRect(center.x.toInt - (w/2), center.y.toInt - (h/2), w, h)
    }
  }

  def fillCircle(gc: Graphics2D)(dc: DrawingContext, center: Point, radius: Double): Unit = {
    dc.fill.foreach{ f =>
      Java2D.setFill(gc, f)
      val r = radius.toInt
      val d = (radius * 2).toInt
      gc.fillOval(center.x.toInt - r, center.y.toInt - r, d, d)
    }
  }
  def strokeCircle(gc: Graphics2D)(dc: DrawingContext, center: Point, radius: Double): Unit = {
    dc.stroke.foreach{ s =>
      Java2D.setStroke(gc, s)
      val r = radius.toInt
      val d = (radius * 2).toInt
      gc.drawOval(center.x.toInt - r, center.y.toInt - r, d, d)
    }
  }

  def fillPolygon(gc: Graphics2D)(dc: DrawingContext, points: Array[Point]): Unit = {
    dc.fill.foreach{ f =>
      Java2D.setFill(gc, f)
      val xs = Array.ofDim[Int](points.size)
      val ys = Array.ofDim[Int](points.size)
      points.zipWithIndex.foreach{ case (pt, idx) =>
        xs(idx) = pt.x.toInt
        ys(idx) = pt.y.toInt
      }
      gc.drawPolygon(xs, ys, points.size)
    }
  }
  def strokePolygon(gc: Graphics2D)(dc: DrawingContext, points: Array[Point]): Unit = {
    dc.stroke.foreach{ s =>
      Java2D.setStroke(gc, s)
      val xs = Array.ofDim[Int](points.size)
      val ys = Array.ofDim[Int](points.size)
      points.zipWithIndex.foreach{ case (pt, idx) =>
        xs(idx) = pt.x.toInt
        ys(idx) = pt.y.toInt
      }
      gc.drawPolygon(xs, ys, points.size)

    }
  }

  def fillClosedPath(gc: Graphics2D)(dc: DrawingContext, center: Point, elements: List[PathElement]): Unit = {
    dc.fill.foreach{ f =>
      Java2D.setFill(gc, f)
      val path = Java2D.toPath2D(center, elements)
      path.closePath()
      gc.fill(path)
    }
  }
  def strokeClosedPath(gc: Graphics2D)(dc: DrawingContext, center: Point, elements: List[PathElement]): Unit = {
    dc.stroke.foreach{ s =>
      Java2D.setStroke(gc, s)
      val path = Java2D.toPath2D(center, elements)
      path.closePath()
      gc.draw(path)
    }
  }

  def fillOpenPath(gc: Graphics2D)(dc: DrawingContext, center: Point, elements: List[PathElement]): Unit = {
    dc.fill.foreach{ f =>
      Java2D.setFill(gc, f)
      val path = Java2D.toPath2D(center, elements)
      gc.fill(path)
    }
  }
  def strokeOpenPath(gc: Graphics2D)(dc: DrawingContext, center: Point, elements: List[PathElement]): Unit = {
    dc.stroke.foreach{ s =>
      Java2D.setStroke(gc, s)
      val path = Java2D.toPath2D(center, elements)
      gc.draw(path)
    }
  }
}
