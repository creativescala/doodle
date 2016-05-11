package doodle
package backend

import doodle.core.{DrawingContext, Image}

trait Canvas[A] {
  def draw(interpreter: Configuration => Interpreter, image: Image): A
}
