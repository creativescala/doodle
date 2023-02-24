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
package reactor
package examples

import cats.effect.unsafe.implicits.global
import doodle.core._
import doodle.image.Image
import doodle.java2d._

object Easing {
  def easeIn(t: Double): Double =
    Math.pow(t, 2.0)

  def easeOut(t: Double): Double =
    1.0 - Math.pow(1.0 - t, 2.0)

  def scale(min: Double, max: Double): Double => Double = {
    val range = max - min
    (x: Double) => min + (x * range)
  }

  val step = (easeIn _).andThen(scale(-300.0, 300.0))
  val reactor =
    Reactor
      .linearRamp()
      .withRender(t =>
        Image.circle(5.0).fillColor(Color.seaGreen).at(step(t), 0.0)
      )

  def go() =
    reactor.run(Frame.default.withSize(600, 600))
}
