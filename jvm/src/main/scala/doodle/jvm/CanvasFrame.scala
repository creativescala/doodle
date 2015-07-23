package doodle
package jvm

import javax.swing.JFrame

class CanvasFrame extends JFrame {
  val panel = new CanvasPanel()

  getContentPane().add(panel)
  pack()
}
