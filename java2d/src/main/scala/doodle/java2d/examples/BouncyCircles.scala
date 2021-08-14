/*
 * Copyright 2015-2020 Noel Welsh
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

object BouncyCircles {
  import cats.implicits._
  import doodle.core._
  import doodle.effect.Writer.Gif
  import doodle.syntax._
  import doodle.java2d.effect._
  import doodle.interact.easing._
  import doodle.interact.syntax._
  import monix.reactive.Observable
  import monix.execution.Scheduler.Implicits.global

  val frame = Frame.size(600, 600).background(Color.darkMagenta)
  val steps = 60 * 10

  def bounce(easing: Easing): Observable[Double] =
    easing.toObservable(steps) ++
      easing.toObservable(steps) ++
      easing.toObservable(steps) ++
      easing.toObservable(steps)

  val animation: Observable[Picture[Unit]] =
    Observable
      .zip6(
        bounce(Easing.linear),
        bounce(Easing.quadratic),
        bounce(Easing.cubic),
        bounce(Easing.sin),
        bounce(Easing.circle),
        bounce(Easing.back)
      )
      .map { case (r1, r2, r3, r4, r5, r6) =>
        circle[Algebra, Drawing](r1 * 85 + 10)
          .strokeColor(Color.magenta.spin(180.degrees))
          .at(r1 * 400 - 200, 250)
          .on(
            circle[Algebra, Drawing](r2 * 85 + 10)
              .strokeColor(Color.magenta.spin(170.degrees))
              .at(r2 * 400 - 200, 150)
          )
          .on(
            circle[Algebra, Drawing](r3 * 85 + 10)
              .strokeColor(Color.magenta.spin(140.degrees))
              .at(r3 * 400 - 200, 50)
          )
          .on(
            circle[Algebra, Drawing](r4 * 85 + 10)
              .strokeColor(Color.magenta.spin(150.degrees))
              .at(r4 * 400 - 200, -50)
          )
          .on(
            circle[Algebra, Drawing](r5 * 85 + 10)
              .strokeColor(Color.magenta.spin(140.degrees))
              .at(r5 * 400 - 200, -150)
          )
          .on(
            circle[Algebra, Drawing](r6 * 85 + 10)
              .strokeColor(Color.magenta.spin(120.degrees))
              .at(r6 * 400 - 200, -250)
          )
          .strokeWidth(2.0)
      }

  def go() =
    animation.animate(frame)

  def write() =
    animation.take(steps.toLong).write[Gif]("bouncy-circles.gif", frame)
}
