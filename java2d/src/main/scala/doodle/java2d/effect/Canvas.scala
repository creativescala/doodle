/*
 * Copyright 2015-2020 Noel Welsh
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
import cats.effect.std.Queue
import cats.effect.unsafe.IORuntime
import doodle.core.Point
import doodle.core.Transform
import fs2.Stream

import java.awt.event._
import java.util.concurrent.atomic.AtomicReference
import javax.swing.JFrame
import javax.swing.Timer
import javax.swing.WindowConstants

/** A [[Canvas]] is an area on the screen to which Pictures can be drawn.
  */
final class Canvas private (
    frame: Frame,
    redrawQueue: Queue[IO, Int],
    mouseClickQueue: Queue[IO, Point],
    mouseMoveQueue: Queue[IO, Point]
)(implicit runtime: IORuntime)
    extends JFrame(frame.title) {
  private val panel = new Java2DPanel(frame)

  /** The current global transform from logical to screen coordinates
    */
  private val currentInverseTx: AtomicReference[Transform] =
    new AtomicReference(Transform.identity)

  /** Draw the given Picture to this [[Canvas]].
    */
  def render[A](picture: Picture[A]): IO[A] = {
    // Possible race condition here setting the currentInverseTx
    def register(
        cb: Either[Throwable, Java2DPanel.RenderResult[A]] => Unit
    ): Unit = {
      // val drawing = picture(algebra)
      // val (bb, rdr) = drawing.runA(List.empty).value
      // val (w, h) = Java2d.size(bb, frame.size)

      // val rr = Java2DPanel.RenderRequest(bb, w, h, rdr, cb)
      panel.render(Java2DPanel.RenderRequest(picture, frame, cb))
    }

    IO.async_(register).map { result =>
      val inverseTx = Java2d.inverseTransform(
        result.boundingBox,
        result.width,
        result.height,
        frame.center
      )
      currentInverseTx.set(inverseTx)
      result.value
    }
  }

  val redraw: Stream[IO, Int] = Stream.fromQueueUnterminated(redrawQueue)
  private val frameRateMs = (1000.0 * (1 / 60.0)).toInt
  private val frameEvent = {

    /** Delay between frames when rendering at 60fps */
    var firstFrame = true
    var lastFrameTime = 0L
    new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        val now = e.getWhen()
        if (firstFrame) {
          firstFrame = false
          lastFrameTime = now
          redrawQueue.offer(0).unsafeRunSync()
          ()
        } else {
          redrawQueue.offer((now - lastFrameTime).toInt).unsafeRunSync()
          lastFrameTime = now
        }
      }
    }
  }
  private val timer = new Timer(frameRateMs, frameEvent)

  val mouseClick: Stream[IO, Point] =
    Stream.fromQueueUnterminated(mouseClickQueue)

  this.addMouseListener(
    new MouseListener {

      def mouseClicked(e: MouseEvent): Unit = {
        val pt = e.getPoint()
        val inverseTx = currentInverseTx.get()
        // ack
        mouseClickQueue
          .offer(inverseTx(Point(pt.getX(), pt.getY())))
          .unsafeRunSync()
        ()
      }

      def mouseEntered(e: MouseEvent): Unit = ()
      def mouseExited(e: MouseEvent): Unit = ()
      def mousePressed(e: MouseEvent): Unit = ()
      def mouseReleased(e: MouseEvent): Unit = ()
    }
  )

  val mouseMove: Stream[IO, Point] =
    Stream.fromQueueUnterminated(mouseMoveQueue)
  this.addMouseMotionListener(
    new MouseMotionListener {

      def mouseDragged(e: MouseEvent): Unit = ()
      def mouseMoved(e: MouseEvent): Unit = {
        val pt = e.getPoint()
        val inverseTx = currentInverseTx.get()
        // ack
        mouseMoveQueue
          .offer(inverseTx(Point(pt.getX(), pt.getY())))
          .unsafeRunSync()
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

object Canvas {

  def apply(frame: Frame)(implicit runtime: IORuntime): IO[Canvas] = {
    import cats.implicits._
    def eventQueue[A]: IO[Queue[IO, A]] = Queue.circularBuffer[IO, A](1)
    (eventQueue[Int], eventQueue[Point], eventQueue[Point]).mapN {
      (redrawQueue, mouseClickQueue, mouseMoveQueue) =>
        new Canvas(frame, redrawQueue, mouseClickQueue, mouseMoveQueue)
    }
  }

}
