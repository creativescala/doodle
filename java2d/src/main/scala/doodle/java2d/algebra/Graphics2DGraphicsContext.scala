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

import doodle.algebra.generic._
import doodle.core.PathElement
import doodle.core.Point
import doodle.core.font.Font
import doodle.core.{Transform => Tx}
import doodle.java2d.algebra.reified.GraphicsContext
import doodle.core.ClosedPath

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage

/** Higher level shape primitives */
object Graphics2DGraphicsContext extends GraphicsContext[Graphics2D] {
  def fillRect(
      gc: Graphics2D
  )(transform: Tx, fill: Fill, width: Double, height: Double): Unit = {
    Java2D.setFill(gc, fill)
    Java2D.withTransform(gc, transform) {
      val w = width.toInt
      val h = height.toInt
      gc.fillRect(-(w / 2), -(h / 2), w, h)
    }
  }
  def strokeRect(
      gc: Graphics2D
  )(transform: Tx, stroke: Stroke, width: Double, height: Double): Unit = {
    Java2D.setStroke(gc, stroke)
    Java2D.withTransform(gc, transform) {
      val w = width.toInt
      val h = height.toInt
      gc.drawRect(-(w / 2), -(h / 2), w, h)
    }
  }

  def fillCircle(
      gc: Graphics2D
  )(transform: Tx, fill: Fill, diameter: Double): Unit = {
    Java2D.setFill(gc, fill)
    Java2D.withTransform(gc, transform) {
      val r = (diameter / 2.0).toInt
      val d = diameter.toInt
      gc.fillOval(-r, -r, d, d)
    }
  }
  def strokeCircle(
      gc: Graphics2D
  )(transform: Tx, stroke: Stroke, diameter: Double): Unit = {
    Java2D.setStroke(gc, stroke)
    Java2D.withTransform(gc, transform) {
      val r = (diameter / 2.0).toInt
      val d = diameter.toInt
      gc.drawOval(-r, -r, d, d)
    }
  }

  def fillPolygon(
      gc: Graphics2D
  )(transform: Tx, fill: Fill, points: Array[Point]): Unit = {
    Java2D.setFill(gc, fill)
    Java2D.withTransform(gc, transform) {
      val xs = Array.ofDim[Int](points.size)
      val ys = Array.ofDim[Int](points.size)
      points.zipWithIndex.foreach { case (pt, idx) =>
        xs(idx) = pt.x.toInt
        ys(idx) = pt.y.toInt
      }
      gc.fillPolygon(xs, ys, points.size)
    }
  }
  def strokePolygon(
      gc: Graphics2D
  )(transform: Tx, stroke: Stroke, points: Array[Point]): Unit = {
    Java2D.setStroke(gc, stroke)
    Java2D.withTransform(gc, transform) {
      val xs = Array.ofDim[Int](points.size)
      val ys = Array.ofDim[Int](points.size)
      points.zipWithIndex.foreach { case (pt, idx) =>
        xs(idx) = pt.x.toInt
        ys(idx) = pt.y.toInt
      }
      gc.drawPolygon(xs, ys, points.size)
    }
  }

  def fillClosedPath(
      gc: Graphics2D
  )(transform: Tx, fill: Fill, elements: List[PathElement]): Unit = {
    Java2D.setFill(gc, fill)
    Java2D.withTransform(gc, transform) {
      val path = Java2D.toPath2D(elements)
      path.closePath()
      gc.fill(path)
    }
  }
  def strokeClosedPath(
      gc: Graphics2D
  )(transform: Tx, stroke: Stroke, elements: List[PathElement]): Unit = {
    Java2D.setStroke(gc, stroke)
    Java2D.withTransform(gc, transform) {
      val path = Java2D.toPath2D(elements)
      path.closePath()
      gc.draw(path)
    }
  }

  def fillOpenPath(
      gc: Graphics2D
  )(transform: Tx, fill: Fill, elements: List[PathElement]): Unit = {
    Java2D.setFill(gc, fill)
    Java2D.withTransform(gc, transform) {
      val path = Java2D.toPath2D(elements)
      gc.fill(path)
    }
  }
  def strokeOpenPath(
      gc: Graphics2D
  )(transform: Tx, stroke: Stroke, elements: List[PathElement]): Unit = {
    Java2D.setStroke(gc, stroke)
    Java2D.withTransform(gc, transform) {
      val path = Java2D.toPath2D(elements)
      gc.draw(path)
    }
  }

  def bitmap(gc: Graphics2D)(transform: Tx, image: BufferedImage): Unit =
    Java2D.withTransform(gc, Tx.verticalReflection.andThen(transform)) {
      gc.drawImage(
        image,
        -(image.getWidth() / 2),
        -(image.getHeight() / 2),
        null
      )
      ()
    }

  // Java2D doesn't support fill for text (without using more complicated APIs,
  // that I currently do have the energy to implement). Hence we don't take a
  // fill parameter here.
  def text(
      gc: Graphics2D
  )(
      transform: Tx,
      stroke: Option[Stroke],
      text: String,
      font: Font,
      bounds: Rectangle2D
  ): Unit = 
    stroke.foreach { s =>
      Java2D.setStroke(gc, s)
      // Our default transform adds reflection around the y-axis (to make positive
      // y moving up). This has the effect of causing our text to be drawn upside
      // down. Hence we add a transformation to undo this.
      Java2D.withTransform(gc, Tx.verticalReflection.andThen(transform)) {
        // Our origin is centered in the bounds. Work out the x and y coordinates
        // of the reference point of the text relative to the origin.
        val x = -bounds.getCenterX()
        val y = -bounds.getCenterY()
        gc.setFont(Java2D.toAwtFont(font))
        gc.drawString(text, x.toFloat, y.toFloat)
      }
    }
  
  def clip(
      gc: Graphics2D
  )(
      transform: Tx,
      img: Drawing[Unit],
      clipPath: ClosedPath
  ): Unit = {
    val clip_area = Java2D.toPath2D(clipPath.elements)
    gc.setClip(clip_area)
    //gc.clip(img)
  }
}
