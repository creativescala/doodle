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
import cats.effect.kernel.Resource
import cats.syntax.all.*
import doodle.core.Point
import fs2.*
import fs2.concurrent.Topic

import javax.swing.SwingUtilities
import scala.concurrent.duration.*
import scala.reflect.ClassTag

/** A [[Canvas]] is an area on the screen to which Pictures can be drawn.
  */
final class Canvas private (
    frame: Frame,
    redrawTopic: Topic[IO, Int],
    mouseClickTopic: Topic[IO, Point],
    mouseMoveTopic: Topic[IO, Point]
) {

  /** Construct the type of event queue we use to send events from Swing to Cats
    * Effect land. We choose a circular buffer queue so that we consume bounded
    * memory if the Cats Effect side does not pull from the queue (which will be
    * a common case), and neither does it block if the queue's capacity is
    * reached.
    */
  private def eventQueue[A: ClassTag]: BlockingCircularQueue[A] =
    BlockingCircularQueue(8)

  private val redrawQueue: BlockingCircularQueue[Int] = eventQueue
  private val mouseClickQueue: BlockingCircularQueue[Point] = eventQueue
  private val mouseMoveQueue: BlockingCircularQueue[Point] = eventQueue

  private var window: Java2dWindow = _

  /** IO that evaluates when the underlying window has been closed. */
  private var windowClosed: IO[Boolean] = _
  SwingUtilities.invokeAndWait(() => {
    window = new Java2dWindow(
      frame,
      16.67.milliseconds,
      redrawQueue,
      mouseClickQueue,
      mouseMoveQueue
    )

    windowClosed = IO.fromCompletableFuture(IO.blocking(window.closed))
  })

  val closed: IO[Unit] = windowClosed.void

  def pump[A](
      queue: BlockingCircularQueue[A],
      topic: Topic[IO, A]
  ): Stream[IO, A] =
    Stream.repeatEval(IO.interruptible(queue.take())).through(topic.publish)

  /** The stream that runs everything the Canvas' internals need to work. You
    * must make sure this is executed if you create a Canvas by hand.
    */
  val stream: Stream[IO, Nothing] = {
    val redraw = pump(redrawQueue, redrawTopic).drain
    val mouseClick =
      pump(mouseClickQueue, mouseClickTopic).drain
    val mouseMove = pump(mouseMoveQueue, mouseMoveTopic).drain
    val closeStream = Stream
      .eval(
        windowClosed >>
          (
            redrawTopic.close,
            mouseClickTopic.close,
            mouseMoveTopic.close
          ).parTupled.void
      )
      .drain

    redraw.merge(mouseClick).merge(mouseMove).merge(closeStream)
  }

  private val interruptWhen = closed.attempt
  val redraw: Stream[IO, Int] =
    redrawTopic.subscribe(4).interruptWhen(interruptWhen)
  val mouseClick: Stream[IO, Point] = mouseClickTopic
    .subscribe(4)
    .interruptWhen(interruptWhen)
  val mouseMove: Stream[IO, Point] =
    mouseMoveTopic.subscribe(4).interruptWhen(interruptWhen)

  /** Draw the given Picture to this [[Canvas]].
    */
  def render[A](picture: Picture[A]): IO[A] = {
    val f = window.render(picture)

    IO.fromCompletableFuture(IO(f))
  }

  def close(): IO[Boolean] = {
    IO(window.close()) >>
      windowClosed
  }
}
object Canvas {

  /** Creates a Canvas Resource from the given Frame.
    *
    * The Resource, when used, will wait for the user to close the window before
    * the use block returns. Call the close() method to programmatically close
    * the window if that is desired.
    */
  def apply(frame: Frame): Resource[IO, Canvas] = {
    (Topic[IO, Int], Topic[IO, Point], Topic[IO, Point])
      .mapN { (redrawTopic, mouseClickTopic, mouseMoveTopic) =>
        new Canvas(frame, redrawTopic, mouseClickTopic, mouseMoveTopic)
      }
      .toResource
      .flatMap(canvas => canvas.stream.compile.drain.background.as(canvas))
      .flatMap(canvas =>
        Resource.make(IO.pure(canvas))(canvas => canvas.closed)
      )
  }
}
