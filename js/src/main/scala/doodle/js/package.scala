package doodle

import doodle.core._

package object js {
  def draw(image: Image, id: String): Unit =
    Draw(image, id)
}