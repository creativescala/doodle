package doodle
package backend

import doodle.core.Image

trait Draw {
  def draw(interpreter: Configuration => Interpreter, image: Image): Unit
}
