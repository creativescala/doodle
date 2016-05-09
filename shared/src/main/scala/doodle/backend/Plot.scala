package doodle
package backend

import doodle.core.{DrawingContext, Image}

trait Canvas {
  def draw(interpreter: Configuration => Interpreter, image: Image): Unit
}
