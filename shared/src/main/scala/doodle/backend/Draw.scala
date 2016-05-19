package doodle
package backend

import doodle.core.{DrawingContext, Image}

trait Draw {
  def draw(interpreter: Configuration => Interpreter, image: Image): Unit
}
