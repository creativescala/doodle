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
import doodle.core.Point
import java.awt.Graphics2D

/** Higher level shape primitives */
object Graphics2DGraphicsContext extends GraphicsContext[Graphics2D] {
  def fillRect(gc: Graphics2D)(dc: DrawingContext, center: Point, width: Double, height: Double): Unit = {
    dc.fill.foreach{ f =>
      Java2D.setFill(gc, f)
      val w = width.toInt
      val h = width.toInt
      gc.fillRect(center.x.toInt - w, center.y.toInt - h, w, h)
    }
  }
  def strokeRect(gc: Graphics2D)(dc: DrawingContext, center: Point, width: Double, height: Double): Unit = {
    dc.stroke.foreach{ s =>
      Java2D.setStroke(gc, s)
      val w = width.toInt
      val h = width.toInt
      gc.fillRect(center.x.toInt - w, center.y.toInt - h, w, h)
    }
  }

  def fillCircle(gc: Graphics2D)(dc: DrawingContext, center: Point, radius: Double): Unit = {
    dc.fill.foreach{ f =>
      Java2D.setFill(gc, f)
      val r = radius.toInt
      gc.fillOval(center.x.toInt - r, center.y.toInt - r, r, r)
    }
  }
  def strokeCircle(gc: Graphics2D)(dc: DrawingContext, center: Point, radius: Double): Unit = {
    dc.stroke.foreach{ s =>
      Java2D.setStroke(gc, s)
      val r = radius.toInt
      gc.drawOval(center.x.toInt - r, center.y.toInt - r, r, r)
    }
  }

  def fillPolygon(gc: Graphics2D)(dc: DrawingContext, points: Array[Point]): Unit = ???
  def strokePolygon(gc: Graphics2D)(dc: DrawingContext, points: Array[Point]): Unit = ???
}
