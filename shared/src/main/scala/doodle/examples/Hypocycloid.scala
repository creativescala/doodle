package doodle
package examples

import doodle.core._
import doodle.random._
import cats.instances.list._
import cats.syntax.traverse._

object Hypocycloid {
  type Hypocycloid = List[(Double, Double, Boolean)]

  val size = 200
  def eval(t: Angle, pattern: Hypocycloid): Vec = {
    def loop(pattern: Hypocycloid): Vec =
      pattern match {
        case Nil => Vec.zero
        case (weight, freq, flipped) :: tail => {
          val angle = (t * freq)
          (if(flipped) Vec(weight * angle.sin, weight * angle.cos)
          else Vec(weight * angle.cos, weight * angle.sin)) + loop(tail)
        }
      }

    loop(pattern) * size
  }

  val randomHypocycloid: Random[Hypocycloid] = {
    val elt: Random[(Double, Double, Boolean)] =
      for {
        w <- Random.double
        f <- Random.int(1, 12)
        p <- Random.discrete((false -> 0.75), (true -> 0.25))
      } yield (w, f, p)

    for {
      n    <- Random.int(1, 7)
      elts <- (0 until n).toList.map(_ => elt).sequence
    } yield elts
  }

  def render(epicycloid: Hypocycloid): Image =
    Image.closedPath(
      PathElement.moveTo(eval(Angle.zero, epicycloid).toPoint) ::
        (BigDecimal(0.0) to 1.0 by 0.001).map{ t =>
          val angle = Angle.turns(t.doubleValue())
          PathElement.lineTo(eval(angle, epicycloid).toPoint)
        }.toList
    )

  def image: Image =
    randomHypocycloid.map(render _).run
}
