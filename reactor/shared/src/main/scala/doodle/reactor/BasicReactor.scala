package doodle
package reactor

import doodle.effect._
import doodle.image.Image
import doodle.language.Basic
import scala.concurrent.duration._

/**
 * A [[Reactor]] that has reasonable defaults and a simple builder style for
 * creating more complicated behaviour.
 */
final case class BasicReactor[A](
  initial: A,
  onTickHandler: A => A = (a: A) => a,
  tickRate: FiniteDuration = FiniteDuration(100, MILLISECONDS),
  renderHandler: A => Image = (_: A) => Image.empty,
  stopHandler: A => Boolean = (_: A) => false
) extends Reactor[A] {
  // Reactor methods -------------------------------------------------

  def onTick(value: A): A =
    onTickHandler(value)

  def render(value: A): Image =
    renderHandler(value)

  def stop(value: A): Boolean =
    stopHandler(value)

  // Builder methods -------------------------------------------------

  def onTick(f: A => A): BasicReactor[A] =
    this.copy(onTickHandler = f)

  def tickRate(duration: FiniteDuration): BasicReactor[A] =
    this.copy(tickRate = duration)

  def render(f: A => Image): BasicReactor[A] =
    this.copy(renderHandler = f)

  def stop(f: A => Boolean): BasicReactor[A] =
    this.copy(stopHandler = f)

  // Make stuff happen -----------------------------------------------

  def image: Image =
    render(initial)

  def step: BasicReactor[A] =
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
}
object BasicReactor {
  /**
   * Create a [[BasicReactor]] with the given initial value.
   */
  def init[A](value: A): BasicReactor[A] =
    BasicReactor(initial = value)

  /**
   * Create a [[BasicReactor]] where the value starts at `start` and goes to
   * `stop` in `step` increments.
   */
  def linearRamp(start: Double = 0.0, stop: Double = 1.0, step: Double = 0.01): BasicReactor[Double] =
    BasicReactor
      .init(start)
      .onTick((x: Double) => x + step)
      .stop((x: Double) => x >= stop)
}
