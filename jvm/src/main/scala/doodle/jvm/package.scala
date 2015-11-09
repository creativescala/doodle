package doodle

import java.awt.Frame
import scala.collection.JavaConversions._

package object jvm {
  def quit(): Unit = {
    Frame.getFrames().foreach(_.dispose)
    System.exit(0)
  }
}
