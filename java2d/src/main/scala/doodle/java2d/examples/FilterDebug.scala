package doodle
package java2d
package examples

import cats.effect.*
import cats.effect.unsafe.implicits.global

import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

object FilterDebug extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val original = circle(50).fillColor(Color.red)
    val blurred = original.blur(3.0)

    val frame = Frame.default
      .withSize(400, 200)
      .withTitle("Filter Debug")

    val picture = original.beside(blurred)

    IO {
      picture.drawWithFrame(frame)
    } *> IO.never.as(ExitCode.Success)
  }
}
