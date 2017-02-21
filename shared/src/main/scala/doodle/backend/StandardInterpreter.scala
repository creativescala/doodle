package doodle
package backend

import doodle.core.{Image, DrawingContext}

/**
  *  The standard interpreter that renders an Image. No special effects or
  *  transformations are applied; hence this is the default or standard
  *  interpreter.
  */
final case class StandardInterpreter(context: DrawingContext, metrics: Metrics) {
  def interpret(image: Image): Finalised =
    Finalised.finalise(image, context, metrics)
}
object StandardInterpreter {
  implicit val interpreter: Configuration => Interpreter = {
    case (dc, metrics) => img => StandardInterpreter(dc, metrics).interpret(img)
  }
}
