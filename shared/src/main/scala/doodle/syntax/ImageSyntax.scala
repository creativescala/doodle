package doodle.syntax

import doodle.core.Image
import doodle.backend.{Canvas, Interpreter}
import scala.language.implicitConversions

class ToImageOps(val image: Image) extends AnyVal {
  def draw(implicit interpreter: Interpreter, canvas: Canvas): Unit =
    interpreter.draw(image, canvas)
}

trait ImageSyntax {
  implicit def imageToImageOps(image: Image): ToImageOps =
    new ToImageOps(image)
}
