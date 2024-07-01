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

package doodle.examples.canvas

import cats.effect.unsafe.implicits.global
import doodle.canvas.{*, given}
import doodle.core.*
import doodle.syntax.all.*
import org.scalajs.dom.CanvasRenderingContext2D

import scala.scalajs.js.annotation.*

@JSExportTopLevel("Experiment")
object Experiment {
  def circle = Picture.circle(100).fillColor(Color.red)

  val drawFunction =
    (ctx: CanvasRenderingContext2D) => {
      ctx.lineWidth = 10;
      ctx.strokeRect(75, 140, 150, 110);
      ctx.fillRect(130, 190, 40, 60);
      ctx.beginPath();
      ctx.moveTo(50, 140);
      ctx.lineTo(150, 60);
      ctx.lineTo(250, 140);
      ctx.closePath();
      ctx.stroke();
    }

  val drawFunction2 = 
    (ctx: CanvasRenderingContext2D) => {
      ctx.fillRect(100, 100, 150, 110);
    }

  val joint = circle.on(raster(300, 250)(drawFunction))

  val joint2 = Picture.circle(50).fillColor(Color.blue)
                  .on(raster(250, 250)(drawFunction2))

  @JSExport
  def draw(mount: String) =
    joint.drawWithFrame(Frame(mount))
}
