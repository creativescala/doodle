package doodle
package reactor

import cats.instances.unit._
import doodle.effect.Renderer
import doodle.image.Image
import doodle.image.syntax._
import doodle.interact.effect.AnimationRenderer
import doodle.interact.syntax._
import doodle.language.Basic
import monix.execution.Scheduler
import monix.reactive.Observable
import scala.concurrent.duration._

/**
 * A reactor is a simple way to express an interactive program. It allows us to
 * write programs in terms of some initial state and transformations of that
 * state in response to inputs and clock ticks.
 *
 * This is the basic interface. See [[Reactor]] for a more user friendly
 * implementation.
 *
 * It is based on * the same abstraction in Pyret.
 */
trait BaseReactor[A] {
  def initial: A
  def onTick(value: A): A
  def tickRate: FiniteDuration
  def render(value: A): Image
  def stop(value: A): Boolean

  /**
   * Run one tick of this reactor, drawing on the given `frame`. Returns the
   * next state, or None if the Reactor has stopped.
   */
  def tick[Alg[x[_]] <: Basic[x], F[_], Frame, Canvas](frame: Frame)(
    implicit e: Renderer[Alg, F, Frame, Canvas]): Option[A] = {
    if(stop(initial)) None
    else {
      (render(initial)).draw(frame)
      val next = onTick(initial)
      Some(next)
    }
  }

  /**
   * Runs this reactor, drawing on the given `frame`, until `stop` indicates
   * it should stop.
   */
  def run[Alg[x[_]] <: Basic[x], F[_], Frame, Canvas](frame: Frame)(
    implicit a: AnimationRenderer[Canvas],
    e: Renderer[Alg, F, Frame, Canvas],
    s: Scheduler): Unit = {
    val frames =
      Observable
        .interval(this.tickRate)
        .flatScan0(this.initial){ (a, _) =>
          if(this.stop(a)) Observable.empty
          else Observable(this.onTick(a))
        }
        .map(a => Image.compile[Alg,F](this.render(a)))

    frames.animate(frame)
  }
}
