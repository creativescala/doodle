package doodle
package jvm

import javax.swing.JFrame

import doodle.backend.{Interpreter, Configuration}
import doodle.core.Image

class CanvasFrame extends JFrame {
  def draw(interpreter: Configuration => Interpreter, image: Image): Unit =
    panel.draw(interpreter, image)

  val panel = new CanvasPanel()

  getContentPane().add(panel)
  pack()
}
