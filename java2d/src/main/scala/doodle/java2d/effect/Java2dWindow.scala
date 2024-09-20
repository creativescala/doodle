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

package doodle.java2d.effect

import doodle.core.Point
import doodle.java2d.Picture
import doodle.java2d.effect.Size.FixedSize

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.concurrent.CompletableFuture
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.Timer
import javax.swing.WindowConstants
import scala.concurrent.duration.FiniteDuration

/** Create a GUI window (a Frame in Swing parlance) around a Java2dPanel. */
final class Java2dWindow(
    frame: Frame,
    frameDelay: FiniteDuration,
    redrawQueue: BlockingCircularQueue[Int],
    mouseClickQueue: BlockingCircularQueue[Point],
    mouseMoveQueue: BlockingCircularQueue[Point]
) extends JFrame(frame.title) {
  def render[A](picture: Picture[A]): CompletableFuture[A] =
    panel.render(picture)

  /** This `CompletedFuture` is completed when this `Java2dWindow` is closed. */
  val closed: CompletableFuture[Boolean] = CompletableFuture()

  // Only call from within the event thread
  private def internalClose(): Unit = {
    println("window.close begin")
    timer.stop()
    dispose()
    closed.complete(true)
    println("window.close end")
  }

  def close(): CompletableFuture[Boolean] = {
    println("Java2dWindow close()")
    SwingUtilities.invokeAndWait(() => internalClose())
    closed
  }

  val panel = Java2DPanel(frame, mouseClickQueue, mouseMoveQueue)

  /** Event listener for redraw events. Translates events into the time since
    * the last frame (TODO: what are the units)?
    */
  private val frameEvent = {

    /** Delay between frames when rendering at 60fps */
    var firstFrame = true
    var lastFrameTime = 0L
    new ActionListener {
      def actionPerformed(e: ActionEvent): Unit = {
        val now = e.getWhen()
        if firstFrame then {
          firstFrame = false
          lastFrameTime = now
          redrawQueue.add(0)
          ()
        } else {
          redrawQueue.add((now - lastFrameTime).toInt)
          lastFrameTime = now
        }
      }
    }
  }

  /** Timer that ticks every frameDelay */
  private val timer = new Timer(frameDelay.toMillis.toInt, frameEvent)
  this.addWindowListener(
    new WindowAdapter {
      override def windowClosed(evt: WindowEvent): Unit = {
        println("Java2dWindow windowClosed")
        internalClose()
        ()
      }
    }
  )

  println("a")
  getContentPane().add(panel)
  frame.size match {
    case FixedSize(width, height) =>
      println("b")
      setSize(width.toInt, height.toInt)
    case _ => ()
  }
  println("c")
  pack()
  setVisible(true)
  setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  repaint()
  timer.start()
  println("end")

}
