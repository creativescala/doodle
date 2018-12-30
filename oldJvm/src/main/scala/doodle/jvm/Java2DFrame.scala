package doodle
package jvm

import doodle.core._
import doodle.backend._
import javax.swing.JFrame

final class Java2DFrame(finaliser: Finaliser, renderer: Renderer)
    extends JFrame
    with Interpreter.Draw {
  val panel = new Java2DPanel(finaliser, renderer)

  getContentPane().add(panel)
  pack()
  repaint()
  setVisible(true)

  def interpret(image: Image): Unit = {
    panel.imageQueue.add(image)
    panel.repaint()
  }
}
object Java2DFrame {
  implicit def java2DDraw: Frame.Draw =
    new Frame.Draw {
      def setup(finaliser: Finaliser, renderer: Renderer): Interpreter[Formats.Screen,Unit] =
        new Java2DFrame(finaliser, renderer)
    }
}
