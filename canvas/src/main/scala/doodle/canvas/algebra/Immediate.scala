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

package doodle.canvas.algebra

import doodle.core.Color
import org.scalajs.dom.CanvasRenderingContext2D

trait Immediate {
  def circle(x: Double, y: Double, radius: Double): Unit
  def ellipse(x: Double, y: Double, width: Double, height: Double): Unit
  def ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double): Unit
  def ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double, counterclockwise: Boolean): Unit
  def fillColor(color: Color): Unit
  def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit
  def rectangle(x: Double, y: Double, width: Double, height: Double): Unit
  def rotate(angle: Double): Unit
  def transform(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double): Unit
  def translate(x: Double, y: Double): Unit
}

class ImmediateImpl(ctx: CanvasRenderingContext2D) extends Immediate {
  def circle(x: Double, y: Double, radius: Double): Unit = {
    val x0 = x - radius / 2.0
    val y0 = y - radius / 2.0
    ctx.beginPath()
    ctx.arc(x0, y0, radius, 0, 2 * Math.PI)
    ctx.stroke()
  }

  def ellipse(x: Double, y: Double, width: Double, height: Double): Unit = {
    val x0 = x - width / 2
    val y0 = y - height / 2
    ctx.beginPath()
    ctx.ellipse(x0, y0, width / 2, height / 2, 0, 0, 2 * Math.PI)
    ctx.stroke()
  }

  def ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double): Unit = {
    val x0 = x - radiusX / 2
    val y0 = y - radiusY / 2
    ctx.beginPath()
    ctx.ellipse(x0, y0, radiusX, radiusY, rotation, startAngle, endAngle)
    ctx.stroke()
  }

  def ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double, counterclockwise: Boolean): Unit = {
    val x0 = x - radiusX / 2
    val y0 = y - radiusY / 2
    ctx.beginPath()
    ctx.ellipse(x0, y0, radiusX, radiusY, rotation, startAngle, endAngle, counterclockwise)
    ctx.stroke()
  }

  def fillColor(color: Color): Unit = {
    ctx.fillStyle = CanvasDrawing.colorToCSS(color)
  }

  def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit = {
    ctx.beginPath()
    ctx.moveTo(x1, y1)
    ctx.lineTo(x2, y2)
    ctx.stroke()
  }

  def rectangle(x: Double, y: Double, width: Double, height: Double): Unit = {
    val x0 = x - width / 2
    val y0 = y - height / 2
    ctx.fillRect(x0, y0, width, height)
  }

  def rotate(angle: Double): Unit = {
    ctx.rotate(angle)
  }

  def transform(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double): Unit = {
    ctx.transform(a, b, c, d, e, f)
  }

  def translate(x: Double, y: Double): Unit = {
    ctx.translate(x, y)
  }
}








