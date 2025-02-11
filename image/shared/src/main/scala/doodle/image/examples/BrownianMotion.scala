/*
 * Copyright 2015 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package image
package examples

import cats.instances.all.*
import cats.syntax.all.*
import doodle.core.*
import doodle.image.Image.*
import doodle.image.syntax.core.*
import doodle.random.*

object BrownianMotion {
  def brownianMotion(start: Point, drift: Vec): Random[Point] =
    jitter(start) map { pt =>
      pt + drift
    }

  def jitter(point: Point): Random[Point] = {
    val noise = Random.normal(0, 5.0)

    (noise, noise) mapN { (dx, dy) =>
      Point.cartesian(point.x + dx, point.y + dy)
    }
  }

  val start = Point.zero
  val drift = Vec(3, 0)

  val smoke: Random[Image] = {
    val alpha = Random.normal(0.5, 0.1)
    val hue = Random.double.map(h => (h * 0.1 + 0.7).turns)
    val saturation = Random.double.map(s => (s * 0.8))
    val lightness = Random.normal(0.4, 0.1)
    val color =
      (hue, saturation, lightness, alpha) mapN { (h, s, l, a) =>
        Color.hsl(h, s, l, a)
      }
    val points = Random.int(3, 7)
    val radius = Random.normal(2, 1)
    val rotation = Random.double.map { r =>
      r.turns
    }
    val shape = (points, radius, rotation).mapN { (pts, r, rot) =>
      star(pts, r, r * 0.5).rotate(rot)
    }

    (shape, color).mapN { (shape, stroke) =>
      shape.strokeColor(stroke).strokeWidth(2).noFill
    }
  }

  def walk(steps: Int): Random[Image] = {
    def iter(
        step: Int,
        start: Random[Point],
        shape: Image
    ): List[Random[Image]] =
      step match {
        case 0 =>
          Nil
        case _ =>
          val here = start.map(shape at _.toVec)
          val next = start flatMap (pt => brownianMotion(pt, drift))
          here :: iter(step - 1, next, shape)
      }

    smoke.flatMap { shape =>
      iter(steps, Random.always(start), shape).sequence.map { imgs =>
        imgs.foldLeft(Image.empty) { _ on _ }
      }
    }
  }

  def walkParticles(nParticles: Int, steps: Int): Random[Image] =
    (1 to nParticles).toList
      .map { _ =>
        walk(steps)
      }
      .sequence
      .map { imgs =>
        imgs.foldLeft(Image.empty) { _ on _ }
      }

  val image: Random[Image] =
    walkParticles(10, 100)
}
