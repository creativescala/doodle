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
import doodle.canvas.algebra.immediate
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
  
  val drawFunctionImmediate = 
    (ctx: immediate) => {
      ctx.fillColor(Color.green);
      ctx.rectangle(100, 100, 150, 110);
    }


  val drawFunction3 = 
    (ctx: CanvasRenderingContext2D) => {
      //ctx.fillColor(Color.green);
      ctx.fillStyle = "green";
      ctx.fillRect(-100, -100, 200, 200);
    }
  
  val drawFunction4 = 
    (ctx: CanvasRenderingContext2D) => {
      ctx.fillStyle = "green";
      ctx.fillRect(-50, -50, 100, 100);
    }
  
  val joint = circle.on(raster(250, 250)(drawFunction))
  val joint3 = circle.above((raster(250, 250)(drawFunction)))
  val joint2 = circle.debug.fillColor(Color.blue)
                  .on(raster(250, 250)(drawFunction).debug)

  val joint4 = (raster(250, 250)(drawFunction).debug).beside(circle.debug)
  val joint5 = (circle.debug).beside(raster(250, 250)(drawFunctionImmediate).debug)

  val circle2 = Picture.circle(200).fillColor(Color.blue)

  val joint6 = (circle2.debug).beside(raster(200, 200)(drawFunction3).debug)
  val joint7 = (raster(200, 200)(drawFunction3).debug).beside(circle2.debug)

  val joint8 = (circle2.debug).on(raster(200, 200)(drawFunction3).debug)

  val joint9 = (circle2.debug).on(raster(200, 200)(drawFunction3).debug)
  val joint10 = (raster(200, 200)(drawFunction3).debug).on(circle2.debug)
  val joint11 = (raster(200, 200)(drawFunction4).debug).on(circle2.debug)
  val jjoint = (joint9).above(joint10).beside(joint11)

  val circle3 = Picture.circle(200).fillColor(Color.blue).at(100, 100)
  val jjoint2 = (circle3.debug).beside(raster(200, 200)(drawFunction3).debug)

  def tri = Picture.triangle(100,50).fillColor(Color.red)
  def tri90 = tri.rotate(90.degrees)
  def tri180 = tri.rotate(180.degrees)
  def tri270 = tri.rotate(270.degrees)

  val joint12 = (tri.above((tri90.beside(raster(200, 200)(drawFunction3)))
                      .above(tri180))).beside(tri270)

  val drawFunction5 = 
    (ctx: CanvasRenderingContext2D) => {
      ctx.beginPath();
      ctx.moveTo(30, 140);
      ctx.lineTo(130, 60);
      ctx.lineTo(230, 140);
      ctx.closePath();
      ctx.stroke();
    }
  
  val joint13 = (raster(250, 250)(drawFunction5).debug).beside(circle.debug)

  @JSExport
  def draw(mount: String) =
    joint5.drawWithFrame(Frame(mount))
}
