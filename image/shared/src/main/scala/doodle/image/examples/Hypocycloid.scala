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

import cats.instances.list.*
import cats.syntax.traverse.*
import doodle.core.*
import doodle.random.{*, given}

object Hypocycloid {
  type Hypocycloid = List[(Double, Double, Boolean)]

  val size = 200
  def eval(t: Angle, pattern: Hypocycloid): Vec = {
    def loop(pattern: Hypocycloid): Vec =
      pattern match {
        case Nil => Vec.zero
        case (weight, freq, flipped) :: tail => {
          val angle = (t * freq)
          (if flipped then Vec(weight * angle.sin, weight * angle.cos)
           else Vec(weight * angle.cos, weight * angle.sin)) + loop(tail)
        }
      }

    loop(pattern) * size.toDouble
  }

  val randomHypocycloid: Random[Hypocycloid] = {
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

  def render(epicycloid: Hypocycloid): Image =
    Image.path(
      ClosedPath(
        PathElement.moveTo(eval(Angle.zero, epicycloid).toPoint) ::
          (BigDecimal(0.0) to 1.0 by 0.001).map { t =>
            val angle = Angle.turns(t.doubleValue)
            PathElement.lineTo(eval(angle, epicycloid).toPoint)
          }.toList
      )
    )

  def image: Image =
    randomHypocycloid.map(render _).run
}
