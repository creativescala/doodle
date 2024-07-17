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
  def fillColor(color: Color): Unit
  def rectangle(x: Double, y: Double, width: Double, height: Double): Unit
  def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit
}

class ImmediateImpl(ctx: CanvasRenderingContext2D) extends Immediate {
  def fillColor(color: Color): Unit = {
    ctx.fillStyle = CanvasDrawing.colorToCSS(color)
  }

  def rectangle(x: Double, y: Double, width: Double, height: Double): Unit = {
    val x0 = x - width / 2
    val y0 = y - height / 2
    ctx.fillRect(x, y, width, height)
  }

  def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit =
    CanvasDrawing { ctx =>
      ctx.beginPath()
      ctx.moveTo(x1, y1)
      ctx.lineTo(x2, y2)
      ctx.closePath()
    }
}







