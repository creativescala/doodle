/*
 * Copyright 2019 Noel Welsh
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
package java2d
package examples

object PulsingCircle {
  import cats.instances.all._
  import doodle.core._
  import doodle.syntax._
  import doodle.java2d.effect._
  import doodle.interact.syntax._
  import monix.reactive.Observable

  val frame = Frame.size(600, 600)

  def circle(diameter: Int): Picture[Unit] =
    Picture { implicit algebra =>
      algebra
        .circle(diameter.toDouble)
        .strokeColor(Color.crimson)
        .strokeWidth(3.0)
    }

  val animation: Observable[Picture[Unit]] =
    Observable
      .repeat(1)
      .scan((3, 10)) { (state, _) =>
        val (inc, diameter) = state
        if (diameter >= 500) (-3, diameter - 3)
        else if (diameter <= 10) (1, diameter + 3)
        else (inc, diameter + inc)
      }
      .map { case (_, d) => circle(d) }

  def go() =
    // animation.animateFrames(frame)(x => println(x))(java2dAnimator, java2dRenderer, java2dCanvasAlgebra, monix.execution.Scheduler.global, implicitly[monix.reactive.ObservableLike[Observable]], implicitly[cats.Monoid[Unit]])
    animation.animateFrames(frame)
}
