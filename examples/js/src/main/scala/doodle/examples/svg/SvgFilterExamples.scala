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
import cats.syntax.all.*
import doodle.algebra.Kernel
import doodle.core.*
import doodle.core.font.Font
import doodle.core.font.FontSize
import doodle.random.{*, given}
import doodle.svg.*
import doodle.syntax.all.*

import scala.scalajs.js.annotation.*

object FilterShapes {
  val redCircle = circle(80).fillColor(Color.red).strokeWidth(0).margin(20)

  val gradientCircle = circle(80)
    .fillGradient(
      Gradient.linear(
        Point(0, 0),
        Point(1, 0),
        List(
          (Color.orange, -1.0),
          (Color.yellow, 0.0),
          (Color.red, 1.0)
        ),
        Gradient.CycleMethod.NoCycle
      )
    )
    .strokeWidth(0)
    .margin(20)

  val complexShape = (circle(60) on square(80))
    .fillColor(Color.purple)
    .strokeColor(Color.black)
    .strokeWidth(3)
    .margin(20)

  val concentricCircles = {
    def loop(count: Int): Picture[Unit] =
      count match {
        case 0 => Picture.empty
        case n =>
          Picture
            .circle(n * 15)
            .fillColor(Color.crimson.spin(10.degrees * n).alpha(0.7.normalized))
            .strokeColor(
              Color.red.spin(15.degrees * n).alpha(0.7.normalized)
            )
            .strokeWidth(4.0)
            .under(loop(n - 1))
      }

    loop(7).margin(20)
  }

  val randomCircle: Random[Picture[Unit]] =
    for {
      pt <- (
        Random.double.map(r => Math.sqrt(r) * 100),
        Random.double.map(_.turns)
      )
        .mapN(Point.polar)
      r <- Random.int(15, 45)
      l <- Random.double(0.3, 0.8)
      c <- Random.double(0.1, 0.4)
      h = (pt.r * 0.35 / 100.0).turns
    } yield Picture
      .circle(r)
      .at(pt)
      .noStroke
      .fillColor(Color.oklch(l, c, h, 0.5))

  val randomCircles = randomCircle.replicateA(200).map(_.allOn.margin(20))

  val layeredShape = (circle(60) on square(100))
    .fillColor(Color.lightBlue)
    .strokeColor(Color.darkBlue)
    .strokeWidth(4)
    .margin(20)

  val embossShape = (regularPolygon(6, 80) on circle(100))
    .fillGradient(
      Gradient.radial(
        Point(0, 0),
        Point(0, 0),
        50,
        List(
          (Color.lightBlue, 0.0),
          (Color.blue, 0.7),
          (Color.darkBlue, 1.0)
        ),
        Gradient.CycleMethod.NoCycle
      )
    )
    .strokeColor(Color.navy)
    .strokeWidth(2)
    .margin(20)
}

@JSExportTopLevel("SvgBlurDemo")
object BlurDemo {
  import FilterShapes.*

  @JSExport
  def draw(mount: String) = {
    val original = redCircle.below(text("original").fillColor(Color.black))
    val blurred =
      redCircle.blur(5.0).below(text("blurred").fillColor(Color.black))

    (original
      .beside(blurred))
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

    blurs.reduce(_.beside(_)).drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgSharpenDemo")
object SharpenDemo {
  import FilterShapes.*

  @JSExport
  def draw(mount: String) = {
    val picture = randomCircles.run()
    val original =
      picture.below(text("original").fillColor(Color.black))
    val sharpened =
      picture
        .sharpen(4.0)
        .below(text("sharpened").fillColor(Color.black))

    (original beside sharpened)
      .drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgEdgeDetectionDemo")
object EdgeDetectionDemo {
  import FilterShapes.*

  @JSExport
  def draw(mount: String) = {
    val original = layeredShape.below(text("original").fillColor(Color.black))
    val edges =
      layeredShape.detectEdges.below(text("detectEdges").fillColor(Color.black))

    (original beside edges)
      .drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgEmbossDemo")
object EmbossDemo {
  import FilterShapes.*

  @JSExport
  def draw(mount: String) = {
    val original =
      concentricCircles.below(text("original").fillColor(Color.black))
    val embossed =
      concentricCircles.emboss.below(text("embossed").fillColor(Color.black))

    (original
      .beside(embossed))
      .drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgDropShadowDemo")
object DropShadowDemo {

  @JSExport
  def draw(mount: String) = {
    val shape = star(5, 50, 25)
      .fillColor(Color.gold)
      .strokeWidth(0)
      .margin(20)

    val original = shape.below(text("original").fillColor(Color.black))
    val shadowed = shape
      .dropShadow(8, 8, 4, Color.black.alpha(Normalized(0.5)))
      .below(text("drop shadow").fillColor(Color.black))

    (original beside shadowed)
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
      .margin(20)

    val original = shape.below(text("original").fillColor(Color.black))
    val filtered = shape
      .blur(2.0)
      .sharpen(1.5)
      .dropShadow(10, 10, 3, Color.black.alpha(Normalized(0.4)))
      .below(text("chained filters").fillColor(Color.black))

    (original beside filtered)
      .drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgCustomKernelDemo")
object CustomKernelDemo {

  @JSExport
  def draw(mount: String) = {
    val customEmboss = Kernel(
      3,
      3,
      IArray(
        -9, -2, 1, -2, 1, 2, 1, 2, 9
      )
    )

    val shape = text("Convolution")
      .font(Font.defaultSerif.bold.italic.size(FontSize.points(36)))
      .fillGradient(
        Gradient.linear(
          Point(0, 0),
          Point(1, 1),
          List(
            (Color.purple, 0.0),
            (Color.hotPink, 0.5),
            (Color.orange, 1.0)
          ),
          Gradient.CycleMethod.NoCycle
        )
      )
      .strokeColor(Color.black)
      .strokeWidth(2)
      .margin(20)

    val original = shape.below(text("original").fillColor(Color.black))
    val enhanced = shape
      .convolve(customEmboss)
      .below(text("custom emboss").fillColor(Color.black))

    (original
      .beside(enhanced))
      .drawWithFrame(Frame(mount))
  }
}

@JSExportTopLevel("SvgBoxBlurComparison")
object BoxBlurComparison {
  import FilterShapes.*

  @JSExport
  def draw(mount: String) = {
    val shape = gradientCircle

    val original = shape.below(text("original").fillColor(Color.black))
    val gaussian = shape
      .blur(5.0)
      .below(text("Gaussian blur").fillColor(Color.black))
    val box = shape
      .boxBlur(5)
      .below(text("box blur").fillColor(Color.black))

    (original beside gaussian beside box)
      .drawWithFrame(Frame(mount))
  }
}
