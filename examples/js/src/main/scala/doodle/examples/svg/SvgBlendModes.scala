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

package doodle.examples.svg

import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.svg.*
import doodle.syntax.all.*

import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("SvgBlendModes")
object BlendModes {

  @JSExport
  def polygons(id: String) = {
    val red = Picture
      .regularPolygon(5, 50)
      .fillColor(Color.indianRed)
      .strokeColor(Color.mediumPurple)
      .strokeWidth(7)

    val green = Picture
      .regularPolygon(7, 50)
      .fillColor(Color.lawnGreen)
      .strokeColor(Color.forestGreen)
      .strokeWidth(7)

    val blue = Picture
      .regularPolygon(9, 50)
      .fillColor(Color.dodgerBlue)
      .strokeColor(Color.lavenderBlush)
      .strokeWidth(7)

    red
      .at(Landmark.centerLeft)
      .screen
      .on(blue.at(Landmark.centerRight).dodge)
      .on(green)
      .drawWithFrame(Frame(id))
  }

  @JSExport
  def table(id: String) = {
    val red = Picture.circle(50).fillColor(Color.crimson).noStroke
    val green = Picture.circle(50).fillColor(Color.lawnGreen).noStroke
    val blue = Picture.circle(50).fillColor(Color.skyBlue).noStroke

    def label(s: String): Picture[Unit] =
      Picture.text(s).noStroke.fillColor(Color.black)

    def makeBlend(
        title: String
    )(blend: Picture[Unit] => Picture[Unit]): Picture[Unit] =
      blend(red.at(Landmark.centerLeft))
        .on(blend(blue.at(Landmark.centerRight)))
        .on(green)
        .above(label(title))
        .margin(25)

    val screen = makeBlend("screen")(_.screen)
    val burn = makeBlend("burn")(_.burn)
    val dodge = makeBlend("dodge")(_.dodge)
    val lighten = makeBlend("lighten")(_.lighten)
    val sourceOver = makeBlend("sourceOver")(_.sourceOver)

    screen
      .beside(burn)
      .beside(dodge)
      .above(lighten.beside(sourceOver))
      .drawWithFrame(Frame(id))
  }
}
