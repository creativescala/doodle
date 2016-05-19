package doodle
package jvm

import javax.swing.JFrame

import doodle.backend.{Interpreter, Configuration}
import doodle.core.Image

final class CanvasFrame(interpreter: Configuration => Interpreter, image: Image) extends JFrame {
  val panel = new CanvasPanel(interpreter, image)

  getContentPane().add(panel)
  pack()
  repaint()
  setVisible(true)
}
