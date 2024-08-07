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
import doodle.canvas.algebra.Immediate
import doodle.core.*
import doodle.syntax.all.*
import org.scalajs.dom.CanvasRenderingContext2D

import scala.scalajs.js.annotation.*

@JSExportTopLevel("Experiment")
object Experiment {

  def circle = Picture.circle(100).fillColor(Color.red)
  
  val drawFunctionImmediate = 
    (ctx: Immediate) => {
      ctx.clipRect(0,0,50,50);
      ctx.clipArc(0, -50, 75, 0);
      ctx.clip();
      ctx.rectangle(0, 0, 100, 100);
      ctx.fill(Color.green);
      ctx.stroke(Color.red);
      ctx.triangle(-50, 50, 50, 50, 0, 80);
      ctx.fill(Color.blue);
      ctx.transform(1, 0.2, 0.8, 1, 0, 0);
      ctx.translate(10,10);
      ctx.ellipse(0,0,50,30);
      ctx.fill(Color.pink);
    }

  val drawFunctionClosedPath = 
    (ctx: Immediate) => {
      ctx.line(0,0,0,50);
      ctx.line(50,50);
      ctx.line(50,0);
      ctx.line(25,-25, true);
      ctx.fill(Color.red)
    }
  
  def drawFunctionPolygon = {
    (ctx: Immediate) => {
      ctx.pentagon(0,0,50);
      ctx.fill(Color.green);
      ctx.stroke(Color.blue)
    }
  }

  def drawFunctionClip = {
    (ctx: Immediate) => {
      ctx.clipArc(0,0,50,0);
      ctx.clipRect(65,65,15,15);
      ctx.clip();
      ctx.rectangle(0,0,100,100);
      ctx.fill(Color.blue)
    }
  }

  val joint = (circle).beside(raster(200, 200)(drawFunctionClip))  

  @JSExport
  def draw(mount: String) =
    joint.drawWithFrame(Frame(mount))
}
