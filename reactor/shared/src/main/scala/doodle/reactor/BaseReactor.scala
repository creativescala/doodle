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

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import cats.instances.unit.*
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
      m: MouseClick[Canvas] with MouseMove[Canvas],
      runtime: IORuntime
  ): Unit = {
    import BaseReactor.*

    frame
      .canvas[Alg, Canvas]()
      .flatMap { canvas =>
        val mouseMove: Stream[IO, Command] =
          canvas.mouseMove.map(pt => MouseMove(pt))
        val mouseClick: Stream[IO, Command] =
          canvas.mouseClick.map(pt => MouseClick(pt))
        val tick: Stream[IO, Command] =
          Stream.fixedRate[IO](this.tickRate).map(_ => Tick)
        val frames = tick
          .merge(mouseMove)
          .merge(mouseClick)
          .scan(this.initial) { (a, cmd) =>
            cmd match {
              case Tick           => this.onTick(a)
              case MouseMove(pt)  => this.onMouseMove(pt, a)
              case MouseClick(pt) => this.onMouseClick(pt, a)
            }
          }
          .takeWhile(a => !this.stop(a))
          .map(a => Image.compile[Alg](this.render(a)))
        frames.animateWithCanvasToIO(canvas)
      }
      .unsafeRunAsync(_ => ())
  }
}
object BaseReactor {
  sealed abstract class Command extends Product with Serializable
  case object Tick extends Command
  final case class MouseMove(location: Point) extends Command
  final case class MouseClick(location: Point) extends Command
}
