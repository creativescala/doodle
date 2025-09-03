package doodle
package java2d
package examples

import cats.effect.*
import cats.effect.unsafe.implicits.global

import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

object Filter extends IOApp {

  def examples: Picture[Unit] = {
    import doodle.java2d.Picture.*

    val baseCircle = circle(100).fillColor(Color.blue)
    val baseSquare = square(80).fillColor(Color.red)

    val row1 = List(
      baseCircle.margin(10),
      baseCircle.blur(5.0).margin(10),
      baseCircle.boxBlur(3).margin(10)
    ).allBeside

    val row2 = List(
      baseSquare.margin(10),
      baseSquare.detectEdges.margin(10),
      baseSquare.sharpen(2.0).margin(10)
    ).allBeside

    val row3 = List(
      baseCircle.emboss.margin(10),
      baseSquare
        .dropShadow(
          offsetX = 5.0,
          offsetY = 5.0,
          blur = 3.0,
          color = Color.black.alpha(0.5.normalized)
        )
        .margin(10)
    ).allBeside

    List(row1, row2, row3).allBeside
  }

  def convolutionExamples: Picture[Unit] = {
    import doodle.java2d.Picture.*
    import doodle.algebra.Kernel

    val baseImage = circle(60)
      .fillColor(Color.orange)
      .beside(
        square(60).fillColor(Color.purple)
      )

    // Custom edge detection kernel
    val customEdgeKernel = Kernel(
      3,
      3,
      IArray(
        -1, -1, -1, -1, 8, -1, -1, -1, -1
      ).map(_.toDouble)
    )

    // Custom blur kernel
    val customBlurKernel = Kernel(
      3,
      3,
      IArray(
        1, 2, 1, 2, 4, 2, 1, 2, 1
      ).map(d => d / 16.0)
    )

    List(
      baseImage.margin(50),
      baseImage.convolve(customEdgeKernel).margin(50),
      baseImage.convolve(customBlurKernel).margin(50)
    ).allBeside
  }

  def run(args: List[String]): IO[ExitCode] = {
    val frame = Frame.default
      .withSize(800, 600)
      .withBackground(Color.lightGray)
      .withTitle("Java2D Filter Examples")

    val picture = examples.above(convolutionExamples.margin(90))

    IO {
      picture.drawWithFrame(frame)
    } *> IO.never.as(ExitCode.Success)
  }
}
