package doodle.syntax

import doodle.core.Image
import doodle.backend.{Draw, Interpreter, Configuration, Save}

trait ImageSyntax {
  implicit class ToImageOps(val image: Image) {
    def draw(implicit draw: Draw, interpreter: Configuration => Interpreter): Unit =
      draw.draw(interpreter, image)

    def save[Format](fileName: String)(implicit save: Save[Format], interpreter: Configuration => Interpreter): Unit =
      save.save[Format](fileName, interpreter, image)
  }
}
