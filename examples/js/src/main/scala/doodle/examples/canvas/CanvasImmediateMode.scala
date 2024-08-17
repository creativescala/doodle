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

@JSExportTopLevel("CanvasImmediateMode")
object Experiment {

  def roof = 
    Picture
      .triangle(200,100)
      .fillColor(Color.blue)
      .strokeColor(Color.black).debug
  
  def drawFunction = 
    (ctx: Immediate) => {
      ctx.rectangle(25, 0, 150, 200)
      ctx.fill(Color.brown)
      ctx.stroke(Color.black)
      ctx.rectangle(75,0,40,50)
      ctx.fill(Color.black)
      ctx.ellipse(100, 175, 40, 20)
      ctx.fill(Color.blue)
      ctx.stroke(Color.black)
      //ctx.text("Doodle !!", 0, 0)
      ctx.star(100,100,25)
      ctx.fill(Color.yellow)
    }
  
  val joint = roof.above(raster(200, 200)(drawFunction))  

  @JSExport
  def draw(mount: String) =
    joint.drawWithFrame(Frame(mount))
}
