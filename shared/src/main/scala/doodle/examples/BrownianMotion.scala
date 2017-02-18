package doodle
package examples

import doodle.core._
import doodle.core.Image._
import doodle.syntax._
import doodle.random._

import cats.instances.list._

import cats.syntax.cartesian._
import cats.syntax.traverse._

object BrownianMotion {
  def brownianMotion(start: Point, drift: Vec): Random[Point] =
    jitter(start) map { pt => pt + drift }

  def jitter(point: Point): Random[Point] = {
    val noise = Random.normal(0, 5.0)

    (noise |@| noise) map { (dx, dy) =>
      Point.cartesian(point.x + dx, point.y + dy)
    }
  }

  val start = Point.zero
  val drift = Vec(3, 0)

  val smoke: Random[Image] = {
    val alpha = Random.normal(0.5, 0.1) map (a => a.normalized)
    val hue = Random.double.map(h => (h * 0.1 + 0.7).turns)
    val saturation = Random.double.map(s => (s * 0.8).normalized)
    val lightness = Random.normal(0.4, 0.1) map (a => a.normalized)
    val color =
      (hue |@| saturation |@| lightness |@| alpha) map {
        (h, s, l, a) => Color.hsla(h, s, l, a)
      }
    val points = Random.int(3,7)
    val radius = Random.normal(2, 1)
    val rotation = Random.double.map { r => r.turns }
    val shape = (points |@| radius |@| rotation).map { (pts, r, rot) =>
      star(pts, r, r * 0.5, rot)
    }

    (shape |@| color).map { (shape, line) =>
      shape.lineColor(line).lineWidth(2).noFill
    }
  }

  def walk(steps: Int): Random[Image] = {
    def iter(step: Int, start: Random[Point], shape: Image): List[Random[Image]] =
      step match {
        case 0 =>
          Nil
        case n =>
          val here = start.map (shape at _.toVec)
          val next = start flatMap (pt => brownianMotion(pt, drift))
          here :: iter(step-1, next, shape)
      }

    smoke.flatMap { shape =>
      iter(steps, Random.always(start), shape).sequenceU.map { imgs =>
        imgs.foldLeft(Image.empty){ _ on _ }
      }
    }
  }

  def walkParticles(nParticles: Int, steps: Int): Random[Image] =
    (1 to nParticles).toList.map { _ => walk(steps) }.sequenceU.map { imgs =>
      imgs.foldLeft(Image.empty){ _ on _ }
    }

  val image: Random[Image] =
    walkParticles(10, 100)
}

