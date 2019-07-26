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
import doodle.core.{Point,Transform}
import doodle.java2d.algebra.Algebra
import java.awt.event._
import java.util.concurrent.atomic.AtomicReference
import javax.swing.{JFrame, Timer, WindowConstants}
import monix.reactive.subjects.PublishSubject

final class Canvas(frame: Frame) extends JFrame(frame.title) {
  val panel = new Java2DPanel(frame)
  val algebra = Algebra()

  /**
   * The current global transform from logical to screen coordinates
   */
  private val currentInverseTx: AtomicReference[Transform] =
    new AtomicReference(Transform.identity)

  def render[A](picture: Picture[A]): IO[A] = {
    // Possible race condition here setting the currentInverseTx
    def register(cb: Either[Throwable, A] => Unit): Unit = {
      val drawing = picture(algebra)
      val (bb, rdr) = drawing.runA(List.empty).value
      val (w, h) = Java2d.size(bb, frame.size)

      val inverseTx = Java2d.inverseTransform(bb, w, h, frame.center)
      currentInverseTx.set(inverseTx)

      val rr = Java2DPanel.RenderRequest(bb, w, h, rdr, cb)
      panel.render(rr)
    }

    IO.async(register)
  }

  val redraw = PublishSubject[Int]()
  val frameRateMs = (1000.0 * (1 / 60.0)).toInt
  val frameEvent = {

    /** Delay between frames when rendering at 60fps */
    var firstFrame = true
    var lastFrameTime = 0L
    new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        val now = e.getWhen()
        if (firstFrame) {
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
      import scala.concurrent.duration.Duration
      import scala.concurrent.Await

      def mouseDragged(e: MouseEvent): Unit = ()
      def mouseMoved(e: MouseEvent): Unit = {
        val pt = e.getPoint()
        val inverseTx = currentInverseTx.get()
        val ack = mouseMove.onNext(inverseTx(Point(pt.getX(), pt.getY())))
        Await.ready(ack, Duration.Inf)
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


  getContentPane().add(panel)
  pack()
  setVisible(true)
  setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  repaint()
  timer.start()

}
