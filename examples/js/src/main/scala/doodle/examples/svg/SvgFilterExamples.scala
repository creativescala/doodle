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
import doodle.algebra.Kernel
import doodle.core.*
import doodle.svg.*
import doodle.syntax.all.*

import scala.scalajs.js.annotation.*

object FilterShapes {
  val redCircle = circle(80).fillColor(Color.red).strokeWidth(0)
  val blueSquare = square(100).fillColor(Color.blue).strokeWidth(0)
  val gradientCircle = circle(80)
    .fillGradient(
      Gradient.linear(
        Point(-40, 0),
        Point(40, 0),
        List(
          (Color.orange, 0.0),
          (Color.yellow, 0.5),
          (Color.red, 1.0)
        ),
        Gradient.CycleMethod.NoCycle
      )
    )
    .strokeWidth(0)

  val complexShape = (circle(60) on square(80))
    .fillColor(Color.purple)
    .strokeColor(Color.black)
    .strokeWidth(3)
}

@JSExportTopLevel("SvgBlurDemo")
object BlurDemo {
  import FilterShapes.*

  @JSExport
  def draw(mount: String) = {
    val original = redCircle
    val blurred = redCircle.blur(5.0)

    (original beside blurred.beside(text("blur(5.0)").fillColor(Color.black)))
      .drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgBlurIntensities")
object BlurIntensities {
  import FilterShapes.*

  @JSExport
  def draw(mount: String) = {
    val blurs = List(0, 2, 5, 10).map { intensity =>
      gradientCircle
        .blur(intensity.toDouble)
        .below(text(s"blur($intensity)").fillColor(Color.black))
    }

    blurs.reduce(_ beside _).drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgSharpenDemo")
object SharpenDemo {
  import FilterShapes.*

  @JSExport
  def draw(mount: String) = {
    val original = complexShape
    val sharpened = complexShape.sharpen(2.0)

    (original beside sharpened.beside(
      text("sharpen(2.0)").fillColor(Color.black)
    ))
      .drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgEdgeDetectionDemo")
object EdgeDetectionDemo {

  @JSExport
  def draw(mount: String) = {
    val shape = (circle(60) on square(100))
      .fillColor(Color.lightBlue)
      .strokeColor(Color.darkBlue)
      .strokeWidth(4)

    val edges = shape.detectEdges

    (shape beside edges.beside(text("detectEdges").fillColor(Color.black)))
      .drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgEmbossDemo")
object EmbossDemo {
  import FilterShapes.*

  @JSExport
  def draw(mount: String) = {
    val original = blueSquare
    val embossed = blueSquare.emboss

    (original beside embossed.beside(text("emboss").fillColor(Color.black)))
      .drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgDropShadowDemo")
object DropShadowDemo {

  @JSExport
  def draw(mount: String) = {
    val shape = star(5, 50, 25).fillColor(Color.gold).strokeWidth(0)
    val shadowed = shape.dropShadow(8, 8, 4, Color.black.alpha(Normalized(0.5)))

    (shape beside shadowed.beside(text("dropShadow").fillColor(Color.black)))
      .drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgChainedFilters")
object ChainedFilters {

  @JSExport
  def draw(mount: String) = {
    val shape = regularPolygon(6, 60)
      .fillColor(Color.crimson)
      .strokeColor(Color.white)
      .strokeWidth(3)

    val filtered = shape
      .blur(2.0)
      .sharpen(1.5)
      .dropShadow(10, 10, 3, Color.black.alpha(Normalized(0.4)))

    (shape beside filtered)
      .below(
        text("Original → blur → sharpen → dropShadow").fillColor(Color.black)
      )
      .drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgCustomKernelDemo")
object CustomKernelDemo {
  import FilterShapes.*

  @JSExport
  def draw(mount: String) = {
    val shape = complexShape

    // Edge enhancement kernel
    val enhance = Kernel(
      3,
      3,
      IArray(
        -1, -1, -1, -1, 9, -1, -1, -1, -1
      )
    )

    val enhanced = shape.convolve(enhance)

    (shape beside enhanced.beside(text("custom kernel").fillColor(Color.black)))
      .drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgBoxBlurComparison")
object BoxBlurComparison {
  import FilterShapes.*

  @JSExport
  def draw(mount: String) = {
    val shape = gradientCircle

    val gaussian =
      shape.blur(5.0).below(text("blur(5.0)").fillColor(Color.black))
    val box = shape.boxBlur(5).below(text("boxBlur(5)").fillColor(Color.black))

    (gaussian beside box).drawWithFrame(Frame(mount))
  }
}

// @testing filter chains
object ChainTest {
  private val chainTest = circle(50)
    .blur(1.0)
    .sharpen(2.0)
    .emboss
    .dropShadow(5, 5, 2, Color.black)
}
