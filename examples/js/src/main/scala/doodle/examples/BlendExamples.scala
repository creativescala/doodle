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

package doodle.examples

import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

trait BlendExamples[Alg <: Blend & Layout & Path & Shape & Style & Text]
    extends BaseExamples[Alg] {

  val polygons: Picture[Alg, Unit] = {
    val red = regularPolygon(5, 50)
      .fillColor(Color.indianRed)
      .strokeColor(Color.mediumPurple)
      .strokeWidth(7)

    val green = regularPolygon(7, 50)
      .fillColor(Color.lawnGreen)
      .strokeColor(Color.forestGreen)
      .strokeWidth(7)

    val blue = regularPolygon(9, 50)
      .fillColor(Color.dodgerBlue)
      .strokeColor(Color.lavenderBlush)
      .strokeWidth(7)

    red
      .at(Landmark.centerLeft)
      .screen
      .on(blue.at(Landmark.centerRight).colorDodge)
      .on(green)
  }

  val table: Picture[Alg, Unit] = {
    val red = circle(50).fillColor(Color.crimson).noStroke
    val green = circle(50).fillColor(Color.lawnGreen).noStroke
    val blue = circle(50).fillColor(Color.skyBlue).noStroke

    def label(s: String): Picture[Alg, Unit] =
      text(s).noStroke.fillColor(Color.black)

    def makeBlend(
        title: String
    )(blend: Picture[Alg, Unit] => Picture[Alg, Unit]): Picture[Alg, Unit] =
      blend(red.at(Landmark.centerLeft))
        .on(blend(blue.at(Landmark.centerRight)))
        .on(green)
        .above(label(title))
        .margin(15)

    val normal = makeBlend("normal")(_.normal)
    val darken = makeBlend("darken")(_.darken)
    val multiply = makeBlend("multiply")(_.multiply)
    val colorBurn = makeBlend("color-burn")(_.colorBurn)
    val lighten = makeBlend("lighten")(_.lighten)
    val screen = makeBlend("screen")(_.screen)
    val colorDodge = makeBlend("color-dodge")(_.colorDodge)
    val overlay = makeBlend("overlay")(_.overlay)
    val softLight = makeBlend("soft-light")(_.softLight)
    val hardLight = makeBlend("hard-light")(_.hardLight)
    val difference = makeBlend("difference")(_.difference)
    val exclusion = makeBlend("exclusion")(_.exclusion)
    val hue = makeBlend("hue")(_.hue)
    val saturation = makeBlend("saturation")(_.saturation)
    val color = makeBlend("color")(_.color)
    val luminosity = makeBlend("luminosity")(_.luminosity)

    normal
      .beside(darken)
      .beside(multiply)
      .above(colorBurn.beside(lighten).beside(screen))
      .above(colorDodge.beside(overlay).beside(softLight))
      .above(hardLight.beside(difference).beside(exclusion))
      .above(hue.beside(saturation).beside(color))
      .above(luminosity)
  }

  val allPictures = List(polygons, table)
}
