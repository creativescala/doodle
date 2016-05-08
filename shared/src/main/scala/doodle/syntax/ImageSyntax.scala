package doodle.syntax

import doodle.core.{DrawingContext,Image}
import doodle.backend.{Canvas, Interpreter, Metrics, Plot}

trait ImageSyntax {
  implicit class ToImageOps(val image: Image) {
    def draw(implicit plotter: Plot, interpreter: (DrawingContext, Metrics) => Interpreter): Unit =
      plotter.draw(interpreter, image)
  }
}
