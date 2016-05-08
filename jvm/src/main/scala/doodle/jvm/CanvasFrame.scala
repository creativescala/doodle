package doodle
package jvm

import javax.swing.JFrame

import doodle.backend.{Interpreter, Metrics}
import doodle.core.{DrawingContext, Image}

class CanvasFrame extends JFrame {
  def draw(interpreter: (DrawingContext, Metrics) => Interpreter, image: Image): Unit =
    panel.draw(interpreter, image)

  val panel = new CanvasPanel()

  getContentPane().add(panel)
  pack()
}
