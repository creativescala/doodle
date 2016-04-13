package doodle.syntax

import doodle.core.Image
import doodle.backend.{Canvas, Interpreter}

trait ImageSyntax {
  implicit class ToImageOps(val image: Image) {
    def draw(implicit interpreter: Interpreter, canvas: Canvas): Unit =
      interpreter.draw(image, canvas)
  }
}
