package doodle.syntax

import doodle.core.{DrawingContext, Image}
import doodle.backend.{Draw, Finalised, Interpreter, Configuration, Save}

trait ImageSyntax {
  implicit class ToImageOps(val image: Image) {
    def draw(implicit draw: Draw, interpreter: Configuration => Interpreter): Unit =
      draw.draw(interpreter, image)

    def finalise: Finalised =
      Finalised.finalise(image, DrawingContext.blackLines)

    def save[Format](fileName: String)(implicit save: Save[Format], interpreter: Configuration => Interpreter): Unit =
      save.save[Format](fileName, interpreter, image)
  }
}
