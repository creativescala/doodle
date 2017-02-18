package doodle
package examples

import doodle.core._
import doodle.core.Image._
import doodle.syntax._
import doodle.random._

import cats.syntax.cartesian._

object DiffusionLimitedAggregation {
  def brownianMotion(start: Point, drift: Vec): Random[Point] =
    jitter(start) map { pt => pt + drift }

  def jitter(point: Point): Random[Point] = {
    val noise = Random.normal(0, 10.0)

    (noise |@| noise) map { (dx, dy) =>
      Point.cartesian(point.x + dx, point.y + dy)
    }
  }

  val start = Point.zero

  val seed: Random[(Point,Vec)] = Random.double.map { t =>
    val angle = t.turns
    val start = Point.polar(200, angle)
    val drift = Vec.polar(3, (angle + 0.5.turns).normalize)

    (start, drift)
  }



  val stuckStart =
    Random.always(List(Point.cartesian(0,0), Point.cartesian(0, 50), Point.cartesian(0, -50)))

  val stickRadius = 5.0

  def distance(pt1: Point, pt2: Point): Double =
    (pt1 - pt2).length

  def isStuck(point: Point, stuck: List[Point]): Boolean =
    stuck.exists(pt => distance(point, pt) < stickRadius)

  def walk(maxSteps: Int, stuck: List[Point]): Random[Option[Point]] = {
    def iter(step: Int, point: Random[Point], drift: Vec): Random[Option[Point]] =
      step match {
        case 0 => Random.always(None)
        case n => point.flatMap { pt =>
          if(isStuck(pt, stuck))
            Random.always(Some(pt))
          else
            iter(step-1, brownianMotion(pt, drift), drift)
        }
      }

    seed flatMap { case (start, drift) =>
      iter(maxSteps, Random.always(start), drift)
    }
  }

  def dla(nParticles: Int, maxSteps: Int, stuck: Random[List[Point]]): Random[List[Point]] =
    nParticles match {
      case 0 => stuck
      case n =>
        val nowStuck =
          stuck flatMap { s =>
            walk(maxSteps, s) map { opt =>
              opt match {
                case Some(pt) => pt :: s
                case None => s
              }
            }
          }
        dla(nParticles-1, maxSteps, nowStuck)
    }

  val smoke: Random[Image] = {
    val alpha = Random.normal(0.5, 0.1) map (a => a.normalized)
    val hue = Random.double.map { h => (h * 0.1 + 0.6).turns }
    val saturation = Random.double.map(s => (s * 0.8).normalized)
    val lightness = Random.normal(0.4, 0.1) map (a => a.normalized)
    val color =
      (hue |@| saturation |@| lightness |@| alpha) map {
        (h, s, l, a) => Color.hsla(h, s, l, a)
      }
    val c = Random.normal(1, 1) map (r => circle(Math.abs(r)))

    (c |@| color) map { (circle, line) => circle.lineColor(line).noFill }
  }

  def makeImage(nParticles: Int, maxSteps: Int): Random[Image] =
    dla(nParticles, maxSteps, stuckStart) flatMap { pts =>
      pts.foldLeft(Random.always(Image.empty)){ (accum, pt) =>
        accum.flatMap { imgs =>
          smoke.map { img => (img at pt.toVec) on imgs }
        }
      }
    }

  val image: Random[Image] = makeImage(1000, 1000)
}

