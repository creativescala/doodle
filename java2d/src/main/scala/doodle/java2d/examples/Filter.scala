package doodle.java2d.examples

import cats.effect.*
import cats.effect.unsafe.implicits.global

import doodle.core.*
import doodle.core.font.Font

import doodle.java2d.*
import doodle.syntax.all.*
import doodle.algebra.Kernel

object Filter extends IOApp {

  def basicFilters: Picture[Unit] = {
    val circle = Picture.circle(80).fillColor(Color.red)
    val square = Picture.square(80).fillColor(Color.blue)

    val row1 = List(
      circle.above(Picture.text("Original").margin(10)),
      circle.blur(5.0).above(Picture.text("Blur 5.0").margin(10)),
      circle.blur(10.0).above(Picture.text("Blur 10.0").margin(10))
    ).map(_.margin(40)).allBeside

    val row2 = List(
      square.above(Picture.text("Original").margin(10)),
      square.detectEdges.above(Picture.text("Edges").margin(10)),
      square.sharpen(2.0).above(Picture.text("Sharpen").margin(10))
    ).map(_.margin(40)).allBeside

    row1.above(row2.margin(40))
  }

  def convolutionExamples: Picture[Unit] = {
    val base = Picture
      .circle(60)
      .fillColor(Color.orange)
      .on(Picture.square(60).fillColor(Color.purple))

    // Edge detection kernel
    val edgeKernel = Kernel(
      3,
      3,
      IArray(
        -1, -1, -1, -1, 8, -1, -1, -1, -1
      ).map(_.toDouble)
    )

    // Emboss
    val examples = List(
      base.above(Picture.text("Original").margin(10)),
      base.emboss.above(Picture.text("Emboss").margin(10)),
      base.convolve(edgeKernel).above(Picture.text("Custom Edge").margin(10))
    ).map(_.margin(40)).allBeside

    examples
  }

  def dropShadowExample: Picture[Unit] = {
    val shape = Picture.star(5, 50, 25).fillColor(Color.green)

    List(
      shape.above(Picture.text("Original").margin(10)),
      shape
        .dropShadow(10, 10, 5, Color.black.alpha(0.5.normalized))
        .above(Picture.text("Drop Shadow").margin(10))
    ).map(_.margin(40)).allBeside
  }

  def run(args: List[String]): IO[ExitCode] = {
    val fullExample = List(
      Picture
        .text("Basic Filters")
        .font(Font.defaultSansSerif.size(18))
        .margin(20),
      basicFilters,
      Picture
        .text("Convolution Examples")
        .font(Font.defaultSansSerif.size(18))
        .margin(20),
      convolutionExamples,
      Picture
        .text("Drop Shadow")
        .font(Font.defaultSansSerif.size(18))
        .margin(20),
      dropShadowExample
    ).allAbove

    val frame = Frame.default
      .withSize(1000, 700)
      .withBackground(Color.white)
      .withTitle("Java2D Filter Examples")

    // Fix: Use drawToIO instead of drawWithFrame
    IO {
      fullExample.drawWithFrame(frame)
    }.flatMap(_ => IO.never)
  }
}
