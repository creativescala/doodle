package doodle
package examples

import doodle.core._
import doodle.core.Image._
import doodle.syntax._
import doodle.random._

import cats.instances.list._

import cats.syntax.cartesian._
import cats.syntax.traverse._

object Spirals {
  def scale(factor: Double): Point => Point =
    (pt: Point) => {
      Point.polar(pt.r * factor, pt.angle)
    }

  def spiral(weight: Angle => Double): Angle => Point =
    (angle: Angle) => Point.polar(weight(angle), angle)

  val linearWeight: Angle => Double =
    angle => angle.toTurns

  val quadraticWeight: Angle => Double =
    angle => angle.toTurns * angle.toTurns

  def roseWeight(k: Int): Angle => Double =
    angle => (angle * k).cos

  val symmetricDecreasingWeight: Angle => Double =
    angle => {
      val turns = {
        val t = angle.toTurns
        if(t < 0.5) t else (t - 0.5)
      }

      (1 - turns)
    }

  val randomSpiral: Random[Angle => Point] =
    Random.oneOf(
      spiral { linearWeight },
      spiral { quadraticWeight },
      spiral { symmetricDecreasingWeight },
      spiral { roseWeight(1) },
      spiral { roseWeight(3) },
      spiral { roseWeight(4) },
      spiral { roseWeight(5) }
    )

  def jitter(point: Point): Random[Point] = {
    val noise = Random.normal(0, 10.0)

    (noise |@| noise) map { (dx, dy) =>
      Point.cartesian(point.x + dx, point.y + dy)
    }
  }

  val smoke: Random[Image] = {
    val alpha = Random.normal(0.5, 0.1) map (a => a.normalized)
    val hue = Random.double.map(h => (h * 0.1 + 0.7).turns)
    val saturation = Random.double.map(s => (s * 0.8).normalized)
    val lightness = Random.normal(0.4, 0.1) map (a => a.normalized)
    val color =
      (hue |@| saturation |@| lightness |@| alpha) map {
        (h, s, l, a) => Color.hsla(h, s, l, a)
      }
    val c = Random.normal(2, 1) map (r => circle(r))

    (c |@| color) map { (circle, line) => circle.lineColor(line).noFill }
  }

  val pts: Random[List[Image]] =
    (1 to 3).toList.map { (i: Int) =>
      randomSpiral flatMap { spiral =>
        ((1 to 720 by 10).toList.map { angle =>
           val pt = (spiral andThen scale(200) andThen jitter)(angle.degrees)

           (smoke |@| pt) map { _ at _.toVec }
         }).sequence
      }
    }.foldLeft(Random.always(List.empty[Image])){ (accum, elt) =>
      accum.flatMap { imgs1 =>
        elt.map { imgs2 => imgs1 ++ imgs2}}
    }

  val image: Random[Image] =
    pts.map(pt => pt.foldLeft(Image.empty){ _ on _ })
}
