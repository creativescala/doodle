package doodle
package backend

import doodle.core.Image

/**
  * An interpreter gives meaning to an Image, usually by drawing it on a Canvas
  *
  * The type parameter `Format` is a phantom type that indicates the kind of output the interpreter produces (e.g. draws to a screen or file).
  *
  * The type parameter `A` is the type of the output is produces (e.g. `Unit` if drawing on the screen, put it may be a more interesting object in other cases.)
  */
trait Interpreter[Format,A] {
  def interpret(image: Image): A
}

object Interpreter {
  /** An interpreter specialised to drawing on the screen. */
  type Draw = Interpreter[Formats.Screen,Unit]
  /**
    * An interpreter specialised to saving to a file.
    *
    * The `String => Unit` result is expected to take a filename and save the
    * image to that file.
    */
  type Save[Format] = Interpreter[Format,String=>Unit]
}
