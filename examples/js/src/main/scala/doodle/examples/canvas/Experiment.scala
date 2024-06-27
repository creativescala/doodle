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

import scala.scalajs.js.annotation.*

@JSExportTopLevel("Experiment")
object Experiment {
  def rect1 = Picture.rectangle(100, 100).fillColor(Color.red)
  def rect2 = Picture.rectangle(100, 100).fillColor(Color.blue)

  val joint = rect1.beside(rect2).raster(10,20)

  @JSExport
  def draw(mount: String) =
    joint.drawWithFrame(Frame(mount))
}