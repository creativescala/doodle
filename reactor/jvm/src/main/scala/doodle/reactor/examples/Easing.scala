package doodle
package reactor
package examples

import doodle.core._
import doodle.image.Image
import doodle.java2d._
import monix.execution.Scheduler.Implicits.global

object Easing {
  def easeIn(t: Double): Double =
    Math.pow(t, 2.0)

  def easeOut(t: Double): Double =
    1.0 - Math.pow(1.0 - t, 2.0)

  def scale(min: Double, max: Double): Double => Double = {
    val range = max - min
    (x: Double) => min + (x * range)
  }

  val step = (easeIn _).andThen(scale(-300.0, 300.0))
  val reactor =
    Reactor
      .linearRamp()
      .withRender(t => Image.circle(5.0).fillColor(Color.seaGreen).at(step(t), 0.0))

  def go() =
    reactor.run(Frame.size(600, 600))
}
