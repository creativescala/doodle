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

import doodle.core._
import doodle.random._
import doodle.syntax.all._

object SierpinskiConfection {
  val reddish: Random[Color] = {
    val hue = Random.double map { d =>
      (d - 0.5) * 0.2
    }
    val saturation = Random.double map { s =>
      s * 0.3 + 0.4
    }
    val lightness = Random.double map { l =>
      l * 0.3 + 0.3
    }

    for {
      h <- hue
      s <- saturation
      l <- lightness
    } yield Color.hsl(h.turns, s, l)
  }

  def triangle(size: Double): Image = {
    Image.triangle(size, size)
  }
  def circle(size: Double): Image = {
    Image.circle(size)
  }

  def shape(size: Double): Random[Image] = {
    for {
      s <- Random.oneOf(triangle(size), circle(size))
      h <- reddish
    } yield s fillColor h
  }

  def sierpinski(n: Int, size: Double): Random[Image] = {
    if (n == 1) {
      shape(size)
    } else {
      val smaller = sierpinski(n - 1, size / 2)
      for {
        a <- smaller
        b <- smaller
        c <- smaller
      } yield a above (b beside c)
    }
  }

  val image = sierpinski(5, 512).run
}
