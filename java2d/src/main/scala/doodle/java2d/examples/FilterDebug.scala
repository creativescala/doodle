package doodle.java2d.examples

import cats.effect.*
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

object FilterDebug extends IOApp {

  def testSimpleBlur: Picture[Unit] = {
    // Simple test: red circle should blur
    val circle = Picture.circle(100).fillColor(Color.red)

    // Place them side by side with labels
    List(
      circle.above(Picture.text("Original")),
      circle.blur(10.0).above(Picture.text("Blur 10.0"))
    ).map(_.margin(50)).allBeside
  }

  def testEdgeDetection: Picture[Unit] = {
    // Blue square with edge detection
    val square = Picture.square(100).fillColor(Color.blue)

    List(
      square.above(Picture.text("Original")),
      square.detectEdges.above(Picture.text("Edge Detection"))
    ).map(_.margin(50)).allBeside
  }

  def testDropShadow: Picture[Unit] = {
    // Green triangle with drop shadow
    val triangle = Picture.triangle(100, 100).fillColor(Color.green)

    List(
      triangle.above(Picture.text("Original")),
      triangle
        .dropShadow(15, 15, 5, Color.black.alpha(0.7.normalized))
        .above(Picture.text("Drop Shadow"))
    ).map(_.margin(50)).allBeside
  }

  def run(args: List[String]): IO[ExitCode] = {
    IO.println("Starting Filter Debug...") *> {
      val tests = List(
        Picture.text("=== BLUR TEST ===").margin(20),
        testSimpleBlur.margin(20),
        Picture.text("=== EDGE DETECTION TEST ===").margin(20),
        testEdgeDetection.margin(20),
        Picture.text("=== DROP SHADOW TEST ===").margin(20),
        testDropShadow.margin(20)
      ).allAbove

      val frame = Frame.default
        .withSize(600, 800)
        .withBackground(Color.lightGray)
        .withTitle("Filter Debug")

      IO {
        import cats.effect.unsafe.implicits.global
        tests.drawWithFrame(frame)
      }
    } *>
      IO.println("Filter Debug window should be visible") *>
      IO.println("Check if filters are applying:") *>
      IO.println("1. Blur should make the red circle fuzzy") *>
      IO.println("2. Edge detection should show edges of blue square") *>
      IO.println(
        "3. Drop shadow should show a shadow offset from green triangle"
      ) *>
      IO.never
  }
}
