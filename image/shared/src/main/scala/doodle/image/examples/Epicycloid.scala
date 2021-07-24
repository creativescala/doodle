package doodle
package image
package examples

import cats.instances.list._
import cats.syntax.traverse._
import doodle.core._
import doodle.random._

import scala.math.BigDecimal

object Epicycloid {
  type Epicycloid = List[(Double, Double, Boolean)]

  val size = 200
  def eval(t: Angle, pattern: Epicycloid): Vec = {
    def loop(pattern: Epicycloid): Vec =
      pattern match {
        case Nil => Vec.zero
        case (weight, freq, flipped) :: tail => {
          val angle = (t * freq)
          (if (flipped) Vec(weight * angle.sin, weight * angle.cos)
           else Vec(weight * angle.cos, weight * angle.sin)) + loop(tail)
        }
      }

    loop(pattern) * size.toDouble
  }

  val randomEpicycloid: Random[Epicycloid] = {
    val elt: Random[(Double, Double, Boolean)] =
      for {
        w <- Random.double
        f <- Random.int(1, 12)
        p <- Random.discrete((false -> 0.75), (true -> 0.25))
      } yield (w, f.toDouble, p)

    for {
      n <- Random.int(1, 7)
      elts <- (0 until n).toList.map(_ => elt).sequence
    } yield elts
  }

  def render(epicycloid: Epicycloid): Image =
    Image.closedPath(
      PathElement.moveTo(eval(Angle.zero, epicycloid).toPoint) ::
        (BigDecimal(0.0) to 1.0 by 0.001).map { t =>
          val angle = Angle.turns(t.doubleValue)
          PathElement.lineTo(eval(angle, epicycloid).toPoint)
        }.toList
    )

  def image: Image =
    randomEpicycloid.map(render _).run
}
