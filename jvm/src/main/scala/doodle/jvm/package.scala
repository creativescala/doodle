package doodle

import doodle.core._

package object jvm {
  def draw(image: Image): Unit =
    DoodleFrame(image).setVisible(true)

  def animate(anim: Animation): Unit =
    DoodleFrame(anim).setVisible(true)
}
