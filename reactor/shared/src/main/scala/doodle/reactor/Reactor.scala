package doodle
package reactor

import cats.instances.unit._
import doodle.effect.{DefaultRenderer, Renderer}
import doodle.image.Image
import doodle.interact.effect.AnimationRenderer
import doodle.interact.syntax._
import doodle.language.Basic
import monix.execution.Scheduler
import monix.reactive.Observable
import scala.concurrent.duration._

final case class Reactor[A](
  initial: A,
  onTick: A => A = (a: A) => a,
  tickRate: FiniteDuration = FiniteDuration(100, MILLISECONDS),
  render: A => Image = (_: A) => Image.empty,
  stop: A => Boolean = (_: A) => false
) {
  // Builder methods -------------------------------------------------

  def onTick(f: A => A): Reactor[A] =
    this.copy(onTick = f)

  def tickRate(duration: FiniteDuration): Reactor[A] =
    this.copy(tickRate = duration)

  def render(f: A => Image): Reactor[A] =
    this.copy(render = f)

  def stop(f: A => Boolean): Reactor[A] =
    this.copy(stop = f)

  // Make stuff happen -----------------------------------------------

  def image: Image =
    this.render(this.initial)

  def step: Reactor[A] =
    this.copy(initial = this.onTick(this.initial))

  def draw[Alg[x[_]] <: Basic[x], F[_], Frame, Canvas](frame: Frame)(
    implicit renderer: Renderer[Alg, F, Frame, Canvas]): Unit = {
    import doodle.image.syntax._
    this.image.draw(frame)(renderer)
  }

  def draw[Alg[x[_]] <: Basic[x], F[_], Frame, Canvas]()(
    implicit renderer: DefaultRenderer[Alg, F, Frame, Canvas]): Unit = {
    import doodle.image.syntax._
    this.image.draw()(renderer)
  }

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
object Reactor {
  def init[A](value: A): Reactor[A] =
    Reactor(initial = value)

  def linearRamp(start: Double = 0.0, stop: Double = 1.0, step: Double = 0.01): Reactor[Double] =
    Reactor
      .init(start)
      .onTick(x => x + step)
      .stop(x => x >= stop)
}
