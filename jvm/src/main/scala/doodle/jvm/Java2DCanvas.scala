package doodle
package jvm

import doodle.core.{DrawingContext, Image}
import doodle.backend.{Canvas, Configuration, Interpreter}

object Java2DCanvas {
  implicit def canvas: Canvas = {
    val frame = new CanvasFrame()
    frame.setVisible(true)

    frame.panel
  }
}
