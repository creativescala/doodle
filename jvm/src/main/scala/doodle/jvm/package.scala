package doodle

import doodle.core._

import java.awt.Frame
import scala.collection.JavaConversions._

package object jvm {
  var frame: DoodleFrame = null

  def draw(image: Image): Unit = {
    DoodleFrame(image).setVisible(true)
  }

  def animate(anim: Animation): Unit = {
    DoodleFrame(anim).setVisible(true)
  }

  def quit(): Unit = {
    Frame.getFrames().foreach(_.dispose)
    System.exit(0)
  }
}
