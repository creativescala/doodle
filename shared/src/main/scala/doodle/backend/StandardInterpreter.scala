package doodle
package backend

import cats.data.Reader

/**
  *  The standard interpreter that renders an Image. No special effects or
  *  transformations are applied; hence this is the default or standard
  *  interpreter.
  */
object StandardInterpreter {
  implicit def interpreter[Format,A](implicit frame: Frame[Format,A]): Interpreter[Format,A] =
    frame.setup(
      Reader{ case (dc, metrics) => image => Finalised.finalise(image, dc, metrics) },
      Reader{ canvas => finalised => Render.render(canvas, finalised) }
    )
}
