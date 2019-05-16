/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package java2d
package effect

import cats.effect.IO
import java.awt.event._
import javax.swing.{JFrame, WindowConstants}
import java.util.concurrent.ScheduledThreadPoolExecutor

final class Java2DFrame(frame: Frame) extends JFrame(frame.title) {
  val panel = new Java2DPanel(frame)

  getContentPane().add(panel)
  pack()
  setVisible(true)
  setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  repaint()

  this.addWindowListener(
    new WindowAdapter {
      override def windowClosed(evt: WindowEvent): Unit =
        timer.shutdown()
    }
  )

  def render[A](picture: Picture[A]): IO[A] =
    panel.render(picture)

  val timer = new ScheduledThreadPoolExecutor(4)
}
object Java2DFrame {
  val nullListener = new ActionListener {
    def actionPerformed(evt: ActionEvent): Unit = ()
  }
}
