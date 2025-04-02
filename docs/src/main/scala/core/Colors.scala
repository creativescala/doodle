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

package docs
package core

import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

object Colors {
  def swatch(color: Color): Picture[Unit] =
    roundedRectangle(50, 50, 5)
      .fillColor(color)
      .strokeWidth(1)
      .strokeColor(Color.black)

  extension (colors: List[Color]) {
    def toSwatches: Picture[Unit] =
      colors.map(c => swatch(c).margin(10, 0)).allBeside
  }

  val basicColors = {
    val red = Color.oklch(0.5, 0.4, 0.degrees) // a vibrant pink
    val redAlpha = Color.oklch(
      0.5,
      0.4,
      0.degrees,
      0.5
    ) // as above, but we also specify the alpha
    val blue = Color.rgb(0, 0, 255) // pure blue
    val blueByte =
      Color.rgb(0.uByte, 0.uByte, 255.uByte) // Using the UnsignedByte type
    val redRgbAlpha = Color.rgb(0, 0, 255, 0.5) // Setting alpha
    val redByteAlpha =
      Color.rgb(0.uByte, 0.uByte, 255.uByte, 0.5.normalized) // Setting alpha

    val colors = List(red, redAlpha, blue, blueByte, redRgbAlpha, redByteAlpha)

    colors.toSwatches
  }

  basicColors.save("core/basic-colors.png")

  val cssColors = {
    List(Color.steelBlue, Color.beige, Color.limeGreen).toSwatches
  }

  cssColors.save("core/css-colors.png")

  val paletteColors = {
    List(
      Tailwind4Colors.amber500,
      Tailwind4Colors.emerald500,
      Tailwind4Colors.sky500,
      CrayolaColors.cinnamonSatin,
      CrayolaColors.grannySmithApple,
      CrayolaColors.cloudySky
    ).toSwatches
  }

  paletteColors.save("core/palette-colors.png")

  val colorTransformations = {
    val spins =
      List(
        Color.aquamarine,
        Color.aquamarine.spin(20.degrees),
        Color.aquamarine.spin(40.degrees),
        Color.aquamarine.spin(60.degrees)
      ).toSwatches

    val lightens =
      List(
        Color.midnightBlue,
        Color.midnightBlue.lightenBy(0.2.normalized),
        Color.midnightBlue.lightenBy(0.4.normalized),
        Color.midnightBlue.lightenBy(0.6.normalized)
      ).toSwatches

    val saturations =
      List(
        Color.paleVioletRed,
        Color.paleVioletRed.saturateBy(0.2),
        Color.paleVioletRed.saturateBy(0.4),
        Color.paleVioletRed.saturateBy(0.6)
      ).toSwatches

    spins
      .margin(0, 0, 10, 0)
      .above(lightens.margin(0, 10))
      .above(saturations.margin(10, 0, 0, 0))
  }

  colorTransformations.save("core/color-transformations.png")

  val box = Picture.rectangle(10, 40).noStroke

  def gradient(count: Int, step: Angle, f: Angle => Color): Picture[Unit] =
    if count == 0 then Picture.empty
    else box.fillColor(f(step * count)).beside(gradient(count - 1, step, f))

  val hsl =
    gradient(40, 5.degrees, angle => Color.hsl(angle, 0.5, 0.5))

  val oklch =
    gradient(40, 5.degrees, angle => Color.oklch(0.7, 0.2, angle + 36.degrees))

  val picture = hsl.above(oklch.margin(0, 20))

  picture.save("core/gradients.png")
}
