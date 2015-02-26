package doodle.jvm

import doodle.core._

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.{JFrame, Timer, WindowConstants}

object DoodleFrame {
  def apply(image: Image): DoodleFrame =
    new DoodleFrame(Animation.static(image))

  def apply(anim: Animation): DoodleFrame =
    new DoodleFrame(anim)
}

class DoodleFrame private(var anim: Animation) extends JFrame("Doodle") with ActionListener {
  val panel = DoodlePanel(anim.draw)
  val timer = new Timer(1000/24, this)

  // setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  getContentPane().add(panel)
  pack()
  timer.start()

  def actionPerformed(evt: ActionEvent): Unit = {
    anim = anim.animate
    panel.image = anim.draw
  }
}
