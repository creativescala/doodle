package doodle
package backend

import doodle.core.Image

trait Save[Format] {
  def save[F <: Format](fileName: String, interpreter: Configuration => Interpreter, image: Image): Unit
}
