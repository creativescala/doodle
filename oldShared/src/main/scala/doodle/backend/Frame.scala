package doodle
package backend

/**
  * Represents some stateful object (e.g. a Java2D frame, or a canvas) that can draw an `Image`. Creates an `Interpreter` given components.
  */
trait Frame[Format,A] {
  def setup(finaliser: Finaliser, renderer: Renderer): Interpreter[Format,A]
}

object Frame {
  /** An frame specialised to drawing on the screen. */
  type Draw = Frame[Formats.Screen,Unit]
  /**
    * A frame specialised to saving to a file.
    *
    * The `String => Unit` result is expected to take a filename and save the
    * image to that file.
    */
  type Save[Format] = Frame[Format,String=>Unit]
}
