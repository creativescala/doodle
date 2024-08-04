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
package reactor

import cats.implicits.*
import cats.effect.IO
import cats.effect.std.Queue
import cats.effect.unsafe.IORuntime
import doodle.core.Point
import doodle.effect.Renderer
import doodle.image.Image
import doodle.image.syntax.all.*
import doodle.interact.algebra.MouseClick
import doodle.interact.algebra.MouseMove
import doodle.interact.effect.AnimationRenderer
import doodle.interact.syntax.all.*
import doodle.language.Basic
import doodle.syntax.renderer.*
import fs2.Stream

import scala.concurrent.duration.*

/** A reactor is a simple way to express an interactive program. It allows us to
  * write programs in terms of some initial state and transformations of that
  * state in response to inputs and clock ticks.
  *
  * This is the basic interface. See [[Reactor]] for a more user friendly
  * implementation.
  *
  * It is based on the same abstraction in Pyret.
  */
trait BaseReactor[A] {
  def initial: A
  def onTick(state: A): A
  def onMouseMove(location: Point, state: A): A
  def onMouseClick(location: Point, state: A): A
  def tickRate: FiniteDuration
  def render(value: A): Image
  def stop(value: A): Boolean

  /** Run one tick of this reactor, drawing on the given `frame`. Returns the
    * next state, or None if the Reactor has stopped.
    */
  def tick[Frame, Canvas](
      frame: Frame
  )(implicit
      e: Renderer[Basic, Frame, Canvas],
      runtime: IORuntime
  ): Option[A] = {
    if stop(initial) then None
    else {
      (render(initial)).drawWithFrame(frame)
      val next = onTick(initial)
      Some(next)
    }
  }

  /** Runs this reactor, drawing on the given `frame`, until `stop` indicates it
    * should stop.
    */
  def run[Alg <: Basic, Frame, Canvas](frame: Frame)(implicit
      a: AnimationRenderer[Canvas],
      e: Renderer[Alg, Frame, Canvas],
      m: MouseClick[Canvas] & MouseMove[Canvas],
      runtime: IORuntime
  ): Unit = {
    import BaseReactor.*

    def mouseEventProducer(
        mouseEventQueue: Queue[IO, MouseEvent],
        canvas: Canvas
    ): IO[Unit] = {
      val mouseMove = canvas.mouseMove.map(pt => MouseMove(pt))
      val mouseClick = canvas.mouseClick.map(pt => MouseClick(pt))

      mouseMove
        .merge(mouseClick)
        .foreach(mouseEventQueue.offer)
        .compile
        .drain
    }

    def tickProducer(
        tickQueue: Queue[IO, A],
        mouseEventQueue: Queue[IO, MouseEvent]
    ): IO[Unit] = {
      Stream
        .fixedRate[IO](this.tickRate)
        .evalScan[IO, A](this.initial)((prev, _) =>
          def drainMouseQueue(a: A): IO[A] =
            for
              mouseEvent <- mouseEventQueue.tryTake
              state <- mouseEvent match
                case Some(MouseMove(pt)) =>
                  drainMouseQueue(this.onMouseMove(pt, a))
                case Some(MouseClick(pt)) =>
                  drainMouseQueue(this.onMouseClick(pt, a))
                case None => IO.pure(a)
            yield state

          for
            mouseState <- drainMouseQueue(prev)
            state = this.onTick(mouseState)
          yield state
        )
        .takeWhile(a => !this.stop(a))
        .foreach(tickQueue.offer)
        .compile
        .drain
    }

    def consumer(tickQueue: Queue[IO, A], canvas: Canvas): IO[Unit] = {
      Stream.unit.repeat
        .evalScan[IO, A](this.initial)((prev, _) =>
          for
            maybeTaken <- tickQueue.tryTake
            state = maybeTaken match
              case Some(a) => a
              case None    => prev
          yield state
        )
        .takeWhile(a => !this.stop(a))
        .map(a => Image.compile[Alg](this.render(a)))
        .animateWithCanvasToIO(canvas)
    }

    (
      for
        canvas <- frame.canvas[Alg, Canvas]()
        tickQueue <- Queue.circularBuffer[IO, A](1)
        // mouseEventQueue <- Queue.circularBuffer[IO, MouseEvent](1)
        mouseEventQueue <- Queue.unbounded[IO, MouseEvent]
        _ <-
          (
            mouseEventProducer(mouseEventQueue, canvas),
            tickProducer(tickQueue, mouseEventQueue),
            consumer(tickQueue, canvas)
          )
            .parMapN((_, _, _) => ())
      yield ()
    ).unsafeRunAsync(_ => ())
  }
}
object BaseReactor {
  sealed abstract class MouseEvent extends Product with Serializable
  final case class MouseMove(location: Point) extends MouseEvent
  final case class MouseClick(location: Point) extends MouseEvent
}
