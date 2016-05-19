package doodle

import java.awt.Frame

package object jvm {
  def quit(): Unit = {
    Frame.getFrames().foreach(_.dispose)
    System.exit(0)
  }
}
