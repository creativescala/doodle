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

import cats.effect.IO
import doodle.algebra.generic.Finalized
import doodle.canvas.Picture
import doodle.canvas.algebra.CanvasAlgebra
import doodle.canvas.algebra.CanvasDrawing
import doodle.core.Transform
import doodle.core.Color
import org.scalajs.dom

import org.scalajs.dom.CanvasRenderingContext2D

trait Immediate {
  def circle(x: Double, y: Double, radius: Double): Unit =
    CanvasDrawing { ctx =>
      ctx.arc(x, y, radius, 0, 2 * Math.PI)
    }

  def ellipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double): Unit =
    CanvasDrawing { ctx =>
      ctx.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle)
    }

  def elllipse(x: Double, y: Double, radiusX: Double, radiusY: Double, rotation: Double, startAngle: Double, endAngle: Double, counterclockwise: Boolean): Unit =
    CanvasDrawing { ctx =>
      ctx.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, counterclockwise)
    }

  def fillColor(color: Color): Unit =
    CanvasDrawing { ctx =>
      ctx.fillStyle = CanvasDrawing.colorToCSS(color)
    }

  def line(x1: Double, y1: Double, x2: Double, y2: Double): Unit =
    CanvasDrawing { ctx =>
      ctx.beginPath()
      ctx.moveTo(x1, y1)
      ctx.lineTo(x2, y2)
      ctx.closePath()
    }

  def rectangle(x: Double, y: Double, width: Double, height: Double): Unit =
    CanvasDrawing { ctx =>
      ctx.strokeRect(x, y, width, height)
    }

  def rotate(angle: Double): Unit =
    CanvasDrawing { ctx =>
      ctx.rotate(angle)
    }

  def transform(a: Double, b: Double, c: Double, d: Double, e: Double, f: Double): Unit =
    CanvasDrawing { ctx =>
      ctx.transform(a, b, c, d, e, f)
    }

  def translate(x: Double, y: Double): Unit =
    CanvasDrawing { ctx =>
      ctx.translate(x, y)
    }

  def triangle(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double): Unit =
    CanvasDrawing { ctx =>
      ctx.beginPath()
      ctx.moveTo(x1, y1)
      ctx.lineTo(x2, y2)
      ctx.lineTo(x3, y3)
      ctx.closePath()
    }
}
