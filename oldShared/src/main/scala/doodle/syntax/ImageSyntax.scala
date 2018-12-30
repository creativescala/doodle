package doodle.syntax

import doodle.core.Image
import doodle.backend.Interpreter

trait ImageSyntax {
  implicit class ToImageOps(val image: Image) {
    def draw(implicit draw: Interpreter.Draw): Unit =
      draw.interpret(image)

    def save[Format](fileName: String)(implicit save: Interpreter.Save[Format]): Unit =
      save.interpret(image)(fileName)
  }
}
