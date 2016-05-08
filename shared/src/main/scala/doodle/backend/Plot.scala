package doodle
package backend

import doodle.core.{DrawingContext, Image}

trait Plot {
  def draw(interpreter: (DrawingContext, Metrics) => Interpreter, image: Image): Unit
}
