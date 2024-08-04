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

package doodle
package image
package examples

import doodle.core.*
import doodle.syntax.angle.*

// Mandelbrot Fractal
// Contributed by Mat Moore -- https://github.com/MatMoore

final case class Complex(real: Double, imaginary: Double) {
  def +(other: Complex) =
    Complex(real + other.real, imaginary + other.imaginary)

  def *(other: Complex) = {
    //   (a + bi) * (c + di)
    // = ac - bd + bci + adi
    Complex(
      real * other.real - imaginary * other.imaginary,
      imaginary * other.real + real * other.imaginary
    )
  }

  def /(factor: Double) = {
    Complex(real / factor, imaginary / factor)
  }

  def distanceSquared(other: Complex) = {
    val realDist = real - other.real
    val imDist = imaginary - other.imaginary
    realDist * realDist + imDist * imDist
  }

  def closeTo(other: Complex, threshold: Double = 10) = {
    distanceSquared(other) <= threshold
  }
}

object Mandelbrot {
  val maxApplys: Int = 50

  trait CellRenderer {
    def color(count: Int): Color
    def shape(size: Int): Image
    def apply(size: Int, count: Int) =
      shape(size) strokeWidth (0) fillColor color(count)
  }

  class PaletteCellRenderer(val palette: List[Color]) extends CellRenderer {
    def color(count: Int) = {
      if count == maxApplys then {
        // Use the first colour for points in the Mandelbrot set
        palette(0)
      } else {
        // Cycle through the rest of the palette for points outside the set
        palette.tail((count - 1) % palette.length)
      }
    }

    def shape(size: Int) = Image.rectangle(size.toDouble, size.toDouble)
  }

  val defaultPalette = Color.black :: (0 to 360 by 5).map { angle =>
    Color.hsl(angle.degrees, 1, 0.4)
  }.toList
  val defaultRenderer = new PaletteCellRenderer(defaultPalette)

  def countUntilDiverges(z: Complex, func: Complex => Complex) = {
    lazy val series: LazyList[Complex] = z #:: series.map(func(_))
    series.takeWhile(_.closeTo(z)).take(maxApplys).length
  }

  def cell(center: Complex, size: Int, renderer: CellRenderer) = {
    val count = countUntilDiverges(center, z => z * z + center)
    renderer(size, count)
  }

  def mandlebrot(
      fractalCenter: Complex,
      domainSize: Complex,
      displaySize: Int,
      renderer: CellRenderer = defaultRenderer,
      minSize: Int = 4
  ): Image = {
    if displaySize <= minSize then {
      cell(fractalCenter, displaySize, renderer)
    } else {
      val cellDisplaySize = displaySize / 2
      val cellDomainSize = domainSize / 2
      val quarterWidth = domainSize.real / 4
      val quarterHeight = domainSize.imaginary / 4
      val centerX = fractalCenter.real
      val centerY = fractalCenter.imaginary

      val topLeft = mandlebrot(
        Complex(centerX - quarterWidth, centerY + quarterHeight),
        cellDomainSize,
        cellDisplaySize,
        renderer
      )
      val topRight = mandlebrot(
        Complex(centerX + quarterWidth, centerY + quarterHeight),
        cellDomainSize,
        cellDisplaySize,
        renderer
      )
      val bottomLeft = mandlebrot(
        Complex(centerX - quarterWidth, centerY - quarterHeight),
        cellDomainSize,
        cellDisplaySize,
        renderer
      )
      val bottomRight = mandlebrot(
        Complex(centerX + quarterWidth, centerY - quarterHeight),
        domainSize / 2,
        cellDisplaySize,
        renderer
      )
      (topLeft beside topRight) above (bottomLeft beside bottomRight)
    }
  }

  val image = mandlebrot(Complex(-0.5, 0), Complex(3, 3), 512)
}
