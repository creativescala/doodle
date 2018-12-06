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
import doodle.core.{PathElement, Point, Transform => Tx}
import java.awt.Graphics2D

/** Higher level shape primitives */
object Graphics2DGraphicsContext extends GraphicsContext[Graphics2D] {
  def fillRect(gc: Graphics2D)(transform: Tx,
                               fill: Fill,
                               width: Double,
                               height: Double): Unit = {
    Java2D.setFill(gc, fill)
    Java2D.withTransform(gc, transform) {
      val w = width.toInt
      val h = height.toInt
      gc.fillRect(-(w / 2), -(h / 2), w, h)
    }
  }
  def strokeRect(gc: Graphics2D)(transform: Tx,
                                 stroke: Stroke,
                                 width: Double,
                                 height: Double): Unit = {
    Java2D.setStroke(gc, stroke)
    Java2D.withTransform(gc, transform) {
      val w = width.toInt
      val h = height.toInt
      gc.drawRect(-(w / 2), -(h / 2), w, h)
    }
  }

  def fillCircle(
      gc: Graphics2D)(transform: Tx, fill: Fill, radius: Double): Unit = {
    Java2D.setFill(gc, fill)
    Java2D.withTransform(gc, transform) {
      val r = radius.toInt
      val d = (radius * 2).toInt
      gc.fillOval(-r, -r, d, d)
    }
  }
  def strokeCircle(
      gc: Graphics2D)(transform: Tx, stroke: Stroke, diameter: Double): Unit = {
    Java2D.setStroke(gc, stroke)
    Java2D.withTransform(gc, transform) {
      val r = (diameter / 2.0).toInt
      val d = diameter.toInt
      gc.drawOval(-r, -r, d, d)
    }
  }

  def fillPolygon(
      gc: Graphics2D)(transform: Tx, fill: Fill, points: Array[Point]): Unit = {
    Java2D.setFill(gc, fill)
    Java2D.withTransform(gc, transform) {
      val xs = Array.ofDim[Int](points.size)
      val ys = Array.ofDim[Int](points.size)
      points.zipWithIndex.foreach {
        case (pt, idx) =>
          xs(idx) = pt.x.toInt
          ys(idx) = pt.y.toInt
      }
      gc.fillPolygon(xs, ys, points.size)
    }
  }
  def strokePolygon(gc: Graphics2D)(transform: Tx,
                                    stroke: Stroke,
                                    points: Array[Point]): Unit = {
    Java2D.setStroke(gc, stroke)
    Java2D.withTransform(gc, transform) {
      val xs = Array.ofDim[Int](points.size)
      val ys = Array.ofDim[Int](points.size)
      points.zipWithIndex.foreach {
        case (pt, idx) =>
          xs(idx) = pt.x.toInt
          ys(idx) = pt.y.toInt
      }
      gc.drawPolygon(xs, ys, points.size)
    }
  }

  def fillClosedPath(gc: Graphics2D)(transform: Tx,
                                     fill: Fill,
                                     elements: List[PathElement]): Unit = {
    Java2D.setFill(gc, fill)
    Java2D.withTransform(gc, transform) {
      val path = Java2D.toPath2D(elements)
      path.closePath()
      gc.fill(path)
    }
  }
  def strokeClosedPath(gc: Graphics2D)(transform: Tx,
                                       stroke: Stroke,
                                       elements: List[PathElement]): Unit = {
    Java2D.setStroke(gc, stroke)
    Java2D.withTransform(gc, transform) {
      val path = Java2D.toPath2D(elements)
      path.closePath()
      gc.draw(path)
    }
  }

  def fillOpenPath(gc: Graphics2D)(transform: Tx,
                                   fill: Fill,
                                   elements: List[PathElement]): Unit = {
    Java2D.setFill(gc, fill)
    Java2D.withTransform(gc, transform) {
      val path = Java2D.toPath2D(elements)
      gc.fill(path)
    }
  }
  def strokeOpenPath(gc: Graphics2D)(transform: Tx,
                                     stroke: Stroke,
                                     elements: List[PathElement]): Unit = {
    Java2D.setStroke(gc, stroke)
    Java2D.withTransform(gc, transform) {
      val path = Java2D.toPath2D(elements)
      gc.draw(path)
    }
  }
}
