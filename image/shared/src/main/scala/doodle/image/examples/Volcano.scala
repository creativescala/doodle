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

import cats.syntax.all.*
import doodle.core.*
import doodle.random.*
import doodle.syntax.all.*

object Volcano {
  def rose(k: Int): Angle => Point =
    (angle: Angle) => {
      Point.cartesian(
        (angle * k.toDouble).cos * angle.cos,
        (angle * k.toDouble).cos * angle.sin
      )
    }

  def scale(factor: Double): Point => Point =
    (pt: Point) => {
      Point.polar(pt.r * factor, pt.angle)
    }

  def jitter(point: Point): Random[Point] = {
    val noise = Random.normal(0, 10.0)

    (noise, noise) mapN { (dx, dy) =>
      Point.cartesian(point.x + dx, point.y + dy)
    }
  }

  val smoke: Random[Image] = {
    val alpha = Random.normal(0.7, 0.3)
    val hue = Random.double.map(h => (h * 0.1).turns)
    val saturation = Random.double.map(s => (s * 0.8))
    val lightness = Random.normal(0.8, 0.4)
    val color =
      (hue, saturation, lightness, alpha) mapN { (h, s, l, a) =>
        Color.hsl(h, s, l, a)
      }
    val c = Random.normal(5, 5) map (r => Image.circle(r))

    (c, color) mapN { (circle, stroke) =>
      circle.strokeColor(stroke).noFill
    }
  }

  def point(
      position: Angle => Point,
      scale: Point => Point,
      jitter: Point => Random[Point],
      image: Random[Image],
      rotation: Angle
  ): Angle => Random[Image] = { (angle: Angle) =>
    {
      val pt = position(angle)
      val scaledPt = scale(pt)
      val jitteredPt = jitter(scaledPt)

      (image, jitteredPt) mapN { (i, pt) =>
        i at pt.toVec.rotate(rotation)
      }
    }
  }

  def iterate(step: Angle): (Angle => Random[Image]) => Random[Image] = {
    (point: Angle => Random[Image]) =>
      {
        def iter(angle: Angle): Random[Image] = {
          if angle > Angle.one then Random.always(Image.empty)
          else (point(angle), iter(angle + step)) mapN { _ on _ }
        }

        iter(Angle.zero)
      }
  }

  val image: Random[Image] = {
    val pts =
      for (i <- 28 to 360 by 39) yield {
        iterate(1.degrees) {
          point(
            rose(5),
            scale(i.toDouble),
            jitter _,
            smoke,
            i.degrees
          )
        }
      }
    val picture = pts.foldLeft(Random.always(Image.empty)) { (accum, img) =>
      (accum, img) mapN { _ on _ }
    }
    val background = (Image.rectangle(650, 650).fillColor(Color.black))

    picture map { _ on background }
  }
}
