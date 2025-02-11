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
import doodle.random.*
import doodle.syntax.all.*

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
    angle => (angle * k.toDouble).cos

  val symmetricDecreasingWeight: Angle => Double =
    angle => {
      val turns = {
        val t = angle.toTurns
        if t < 0.5 then t else (t - 0.5)
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

    (noise, noise) mapN { (dx, dy) =>
      Point.cartesian(point.x + dx, point.y + dy)
    }
  }

  val smoke: Random[Image] = {
    val alpha = Random.normal(0.5, 0.1)
    val hue = Random.double.map(h => (h * 0.1 + 0.7).turns)
    val saturation = Random.double.map(s => (s * 0.8))
    val lightness = Random.normal(0.4, 0.1)
    val color =
      (hue, saturation, lightness, alpha) mapN { (h, s, l, a) =>
        Color.hsl(h, s, l, a)
      }
    val c = Random.normal(2, 1) map (r => Image.circle(r))

    (c, color).mapN { (circle, stroke) =>
      circle.strokeColor(stroke).noFill
    }
  }

  val pts: Random[List[Image]] =
    (1 to 3).toList
      .map { (_: Int) =>
        randomSpiral flatMap { spiral =>
          ((1 to 720 by 10).toList.map { angle =>
            val pt = (spiral andThen scale(200) andThen jitter)(angle.degrees)

            (smoke, pt) mapN { _ at _.toVec }
          }).sequence
        }
      }
      .foldLeft(Random.always(List.empty[Image])) { (accum, elt) =>
        accum.flatMap { imgs1 =>
          elt.map { imgs2 =>
            imgs1 ++ imgs2
          }
        }
      }

  val image: Random[Image] =
    pts.map(pt => pt.foldLeft(Image.empty) { _ on _ })
}
