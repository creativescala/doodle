package doodle
package examples

import doodle.core._
import doodle.syntax._
import doodle.random._

object Smoke {
  val emitter =
    Random.normal(0.0, 5.0) flatMap { x =>
      Random.normal(0.0, 5.0) map { y =>
        Point(x, y)
      }
    }

  val drift = Vec(20, 0)

  def perturb(point: Point): Random[Point] =
    Random.normal(0.0, 5.0) flatMap { x =>
      Random.normal(0.0, 5.0) map { y =>
        point + Vec(x, y) + drift
      }
    }

  def cool(color: Color): Random[Color] =
    Random.double map (_ * 0.2) map (_.normalized) map { d =>
      color.desaturateBy(d).fadeOutBy(d).spin((d.get * -100).degrees)
    }

  def widen(radius: Double): Random[Double] =
    Random.double map { r => (r * 2) + radius }

  def particle(point: Point, color: Color, radius: Double): Image =
    Image.circle(radius).fillColor(color).noLine.at(point.toVec)

  def step(point: Point, color: Color, radius: Double): Random[(Point, Color, Double)] =
    perturb(point) flatMap { pt =>
      widen(radius) flatMap { r =>
        cool(color) map { c =>
          (pt, c, r)
        }
      }
    }

  def randomWalk(steps: Int): Random[Image] = {
    def loop(count: Int, point: Point, color: Color, radius: Double): Random[Image] =
      count match {
        case 0 => Random.always(particle(point, color, radius))
        case n =>
          val img = particle(point, color, radius)
          step(point, color, radius) flatMap { updated =>
            val (pt, c, r) = updated
            loop(n-1, pt, c, r).map(accum => img on accum)
          }
      }

    emitter.flatMap(pt => loop(steps, pt, Color.yellow.alpha(0.7.normalized), 3))
  }

  val image =
    (1 to 20).foldLeft(Random.always(Image.empty)){ (accum, _) =>
      accum.flatMap(a => randomWalk(20).map(i => i on a))
    }
}
