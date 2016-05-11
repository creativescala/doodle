package doodle.syntax

import doodle.core.Image
import doodle.backend.{Canvas, Interpreter, Configuration}

trait ImageSyntax {
  implicit class ToImageOps(val image: Image) {
    def draw[A](implicit canvas: Canvas[A], interpreter: Configuration => Interpreter): A =
      canvas.draw(interpreter, image)
  }
}
