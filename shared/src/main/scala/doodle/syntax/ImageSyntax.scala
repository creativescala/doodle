package doodle.syntax

import doodle.core.Image
import doodle.backend.{Canvas, Interpreter, Configuration}

trait ImageSyntax {
  implicit class ToImageOps(val image: Image) {
    def draw(implicit canvas: Canvas, interpreter: Configuration => Interpreter): Unit =
      canvas.draw(interpreter, image)
  }
}
