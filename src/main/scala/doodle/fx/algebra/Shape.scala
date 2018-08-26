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

import cats.data.Kleisli
import cats.effect.IO
import doodle.layout.BoundingBox
import javafx.geometry.Point2D
import javafx.scene.canvas.GraphicsContext

/** Higher level shape primitives */
trait Shape extends doodle.algebra.Shape[WithContext,Unit] {
  def rectangle(width: Double, height: Double): WithContext[Unit] =
    WithContext.now{ (gc, dc, tx) =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = BoundingBox.centered(strokeWidth + width, strokeWidth + height)
      val w = width / 2.0
      val h = height / 2.0

      val result =
        Kleisli{ (origin: Point2D) =>
          val left = origin.getX() - w
          val top  = origin.getY() - h
          // println(s"origin: ${origin}, left: ${left}, top: ${top}, width: ${width}, height: ${height}")
          render(gc, dc){ gc =>
            gc.fillRect(left, top, width, height)
          }{ gc =>
            gc.strokeRect(left, top, width, height)
          }
        }

      (bb, result)
    }

  def square(width: Double): WithContext[Unit] =
    rectangle(width, width)

  def triangle(width: Double, height: Double): WithContext[Unit] =
    WithContext.now{ (gc, dc) =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val bb = BoundingBox.centered(strokeWidth + width, strokeWidth + height)

      val w = width / 2.0
      val h = height / 2.0

      val result =
        Kleisli{ (origin: Point2D) =>
          val xPoints = Array[Double](origin.getX() - w, origin.getX(), origin.getX() + w)
          val yPoints = Array[Double](origin.getY() + h, origin.getY() - h, origin.getY() + h)
          render(gc, dc){ gc =>
            gc.fillPolygon(xPoints, yPoints, 3)
          }{ gc =>
            gc.strokePolygon(xPoints, yPoints, 3)
          }
        }

      (bb, result)
    }

  def circle(radius: Double): WithContext[Unit] =
    WithContext.now{ (gc, dc) =>
      val strokeWidth = dc.strokeWidth.getOrElse(0.0)
      val diameter = radius * 2.0
      val bb = BoundingBox.centered(strokeWidth + diameter, strokeWidth + diameter)
      val result =
        Kleisli{ (origin: Point2D) =>
          render(gc, dc){ gc =>
            gc.fillOval(origin.getX() - radius, origin.getY() - radius, radius, radius)
          }{ gc =>
            gc.strokeOval(origin.getX() - radius, origin.getY() - radius, radius, radius)
          }
        }

      (bb, result)
    }

  def render(gc: GraphicsContext, dc: FxContext)(fill: GraphicsContext => Unit)(stroke: GraphicsContext => Unit): IO[Unit] =
    IO {
      setupGraphicsContext(gc, dc)
      dc.fill.foreach(_ => fill(gc))
      dc.stroke.foreach(_ => stroke(gc))
    }

  def setupGraphicsContext(gc: GraphicsContext, dc: FxContext): Unit = {
    dc.blendMode.map(bm => gc.setGlobalBlendMode(bm))
    dc.stroke.foreach{ stroke =>
      gc.setLineWidth(stroke.width)
      gc.setStroke(stroke.color)
    }
    dc.fill.foreach{ fill =>
      gc.setFill(fill.color)
    }
  }
}
