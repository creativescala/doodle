package doodle
package jvm

import doodle.core.{DrawingContext, Image}
import doodle.backend.{Metrics,Interpreter,Plot}

object Java2DCanvas {
  implicit def plot: Plot =
    new Plot {
      val frame = new CanvasFrame()
      frame.setVisible(true)

      def draw(interpreter: (DrawingContext, Metrics) => Interpreter, image: Image): Unit = {
        frame.panel.draw(interpreter, image)
        frame.panel.repaint()
      }

    }
}
