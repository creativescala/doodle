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
  def arc(x: Int, y: Int, radius: Int, startAngle: Double, endAngle: Double): Unit
  def arc(x: Int, y: Int, radius: Int, startAngle: Double, endAngle: Double, anticlockwise: Boolean): Unit
  def arcTo(x1: Int, y1: Int, x2: Int, y2: Int, radius: Int): Unit
  def bezierCurveTo(cp1x: Int, cp1y: Int, cp2x: Int, cp2y: Int, x: Int, y: Int): Unit
  def beginPath(): Unit
  def clearRect(x: Int, y: Int, width: Int, height: Int): Unit
  def clip(x: Int, y: Int, radius: Int, startAngle: Double): Unit
  def closePath(): Unit
  def circle(x: Double, y: Double, radius: Double): Unit
  def ellipse(x: Double, y: Double, width: Double, height: Double): Unit
  def ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double): Unit
  def ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double, counterclockwise: Boolean): Unit
  def fillColor(color: Color): Unit
  def text(text: String, x: Double, y: Double): Unit
  def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit
  def quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double): Unit
  def rectangle(x: Double, y: Double, width: Double, height: Double): Unit
  def rotate(angle: Double): Unit
  def roundedRectangle(x: Int, y: Int, width: Int, height: Int, radius: Int): Unit
  def stroke(color: Color): Unit
  def strokeText(text: String, x: Double, y: Double): Unit
  def transform(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double): Unit
  def translate(x: Double, y: Double): Unit
  def triangle(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double): Unit
}

class ImmediateImpl(ctx: CanvasRenderingContext2D) extends Immediate {
  def arc(x: Int, y: Int, radius: Int, startAngle: Double, endAngle: Double): Unit = {
    ctx.beginPath()
    ctx.arc(x, y, radius, startAngle, endAngle)
    ctx.stroke()
  }

  def arc(x: Int, y: Int, radius: Int, startAngle: Double, endAngle: Double, anticlockwise: Boolean): Unit = {
    val x0 = x - radius / 2
    val y0 = y - radius / 2
    ctx.beginPath()
    ctx.arc(x0, y0, radius, startAngle, endAngle, anticlockwise)
    ctx.stroke()
  }

  def arcTo(x1: Int, y1: Int, x2: Int, y2: Int, radius: Int): Unit = {
    ctx.beginPath()
    ctx.arcTo(x1, y1, x2, y2, radius)
    ctx.stroke()
  }

  def bezierCurveTo(cp1x: Int, cp1y: Int, cp2x: Int, cp2y: Int, x: Int, y: Int): Unit = {
    ctx.beginPath()
    ctx.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y)
    ctx.stroke()
  }

  def beginPath(): Unit = {
    ctx.beginPath();
  }

  def clearRect(x: Int, y: Int, width: Int, height: Int): Unit = {
    val x0 = x / 2
    val y0 = y / 2
    ctx.clearRect(x0, y0, width, height)
  }

  def clip(x: Int, y: Int, radius: Int, startAngle: Double): Unit = {
    val x0 = x - radius / 2
    val y0 = y - radius / 2
    ctx.beginPath()
    ctx.arc(x0, y0, radius, startAngle, 2 * Math.PI)
    ctx.clip()
  }

  def closePath(): Unit = {
    ctx.closePath()
  }

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
    ctx.fill()
  }

  def ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double): Unit = {
    val x0 = x - radiusX / 2
    val y0 = y - radiusY / 2
    ctx.beginPath()
    ctx.ellipse(x0, y0, radiusX, radiusY, rotation, startAngle, endAngle)
    ctx.fill()
  }

  def ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double, counterclockwise: Boolean): Unit = {
    val x0 = x - radiusX / 2
    val y0 = y - radiusY / 2
    ctx.beginPath()
    ctx.ellipse(x0, y0, radiusX, radiusY, rotation, startAngle, endAngle, counterclockwise)
    ctx.fill()
  }

  def fillColor(color: Color): Unit = {
    ctx.fillStyle = CanvasDrawing.colorToCSS(color)
    ctx.fill()
  }

  def text(text: String, x: Double, y: Double): Unit = {
    val x0 = x - text.length * 2
    ctx.fillText(text, x0, y)
  }

  def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit = {
    ctx.beginPath()
    ctx.moveTo(x1, y1)
    ctx.lineTo(x2, y2)
    ctx.stroke()
  }

  def quadraticCurveTo(cpx: Double, cpy: Double, x: Double, y: Double): Unit = {
    ctx.beginPath()
    ctx.quadraticCurveTo(cpx, cpy, x, y)
    ctx.stroke()
  }

  def rectangle(x: Double, y: Double, width: Double, height: Double): Unit = {
    val x0 = x - width / 2
    val y0 = y - height / 2
    ctx.beginPath()
    ctx.rect(x0, y0, width, height)
  }

  def rotate(angle: Double): Unit = {
    ctx.rotate(angle)
  }

  def roundedRectangle(x: Int, y: Int, width: Int, height: Int, radius: Int): Unit = {
    val x0 = x - width / 2
    val y0 = y - height / 2
    ctx.beginPath()
    ctx.moveTo(x0 + radius, y0)
    ctx.arcTo(x0 + width, y0, x0 + width, y0 + height, radius)
    ctx.arcTo(x0 + width, y0 + height, x0, y0 + height, radius)
    ctx.arcTo(x0, y0 + height, x0, y0, radius)
    ctx.arcTo(x0, y0, x0 + width, y0, radius)
    ctx.fill()   
  }

  def stroke(color: Color): Unit = {
    ctx.strokeStyle = CanvasDrawing.colorToCSS(color)
    ctx.stroke()
  }

  def strokeText(text: String, x: Double, y: Double): Unit = {
    val x0 = x - text.length * 2
    ctx.strokeText(text, x0, y)
  }

  def transform(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double): Unit = {
    ctx.transform(a, b, c, d, e, f)
  }

  def translate(x: Double, y: Double): Unit = {
    ctx.translate(x, y)
  }

  def triangle(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double): Unit = {
    ctx.beginPath()
    ctx.moveTo(x1, y1)
    ctx.lineTo(x2, y2)
    ctx.lineTo(x3, y3)
    ctx.lineTo(x1, y1)
    ctx.fill()
  }
}








