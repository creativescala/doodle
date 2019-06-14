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
import doodle.core.Point
import java.awt.event._
import javax.swing.{JFrame, Timer, WindowConstants}
import monix.reactive.subjects.PublishSubject

final class Canvas(frame: Frame) extends JFrame(frame.title) {
  val panel = new Java2DPanel(frame)

  val redraw = PublishSubject[Int]()
  val frameRateMs = (1000.0 * (1 / 60.0)).toInt
  val frameEvent = {
    /** Delay between frames when rendering at 60fps */
    var firstFrame = true
    var lastFrameTime = 0L
    new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        val now = e.getWhen()
        if(firstFrame) {
          firstFrame = false
          lastFrameTime = now
          redraw.onNext(0)
          ()
        } else {
          redraw.onNext((now - lastFrameTime).toInt)
          lastFrameTime = now
        }
      }
    }
  }
  val timer = new Timer(frameRateMs, frameEvent)


  val mouseMove = PublishSubject[Point]()
  this.addMouseMotionListener(
    new MouseMotionListener {
      def mouseDragged(e: MouseEvent): Unit = ()
      def mouseMoved(e: MouseEvent): Unit = {
        val pt = e.getPoint()
        // TODO: Transform to Doodle's coordinate system
        mouseMove.onNext(Point(pt.getX(), pt.getY()))
        ()
      }
    }
  )

  this.addWindowListener(
    new WindowAdapter {
      override def windowClosed(evt: WindowEvent): Unit =
        timer.stop()
    }
  )

  def render[A](picture: Picture[A]): IO[A] =
    panel.render(picture)

  getContentPane().add(panel)
  pack()
  setVisible(true)
  setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  repaint()
  timer.start()

}
