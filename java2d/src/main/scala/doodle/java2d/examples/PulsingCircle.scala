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

  val frame = Frame.size(600, 600).background(Color.midnightBlue)

  val strokeWidth = 9.0
  val gapWidth = 6.0
  val minimumDiameter = gapWidth + strokeWidth
  val maxNumberOfCircles = 15

  def circle(count: Int): Picture[Unit] =
    count match {
      case 0 =>
        Picture{ implicit algebra =>
          algebra.circle(minimumDiameter.toDouble)
            .noFill
            .strokeWidth(strokeWidth)
        }
      case n =>
        Picture{ implicit algebra =>
          algebra.circle((n * 2 * (strokeWidth + gapWidth) + minimumDiameter))
            .noFill
            .strokeWidth(strokeWidth)
        }
    }

  def background(count: Int): Picture[Unit] = {
    def iter(count: Int): Picture[Unit] =
      count match {
        case 0 =>
          circle(count)
        case n =>
          circle(count).on(iter(n - 1))
      }

    iter(count).strokeWidth(strokeWidth.toDouble).strokeColor(Color.darkGray)
  }

  def pulse(count: Int): Picture[Unit] =
    count match {
      case 0 => circle(0).strokeColor(Color.crimson)
      case 1 =>
        circle(1)
          .strokeColor(Color.crimson)
          .on(circle(0).strokeColor(Color.crimson.spin(30.degrees)))
      case n =>
        circle(n)
          .strokeColor(Color.crimson)
          .on(circle(n-1)
                .strokeColor(Color.crimson.spin(30.degrees)))
          .on(circle(n-2)
                .strokeColor(Color.crimson.spin(60.degrees)))
    }

  val animation: Observable[Picture[Unit]] =
    Observable
      .repeat(1)
      .scan((1, 0)) { (state, _) =>
        val (inc, count) = state
        if (count >= maxNumberOfCircles) (-1, maxNumberOfCircles - 1)
        else if (count <= 0) (1, 1)
        else (inc, count + inc)
      }
      .map { case (_, c) => pulse(c).on(background(maxNumberOfCircles)) }

  def go() =
    animation.animateFrames(frame)


  // import doodle.java2d.effect._
  // import java.io.File
  // println("About to save")
  // Java2dAnimationWriter.write(new File("pulsing-circle-2.gif"), Frame.size(600, 600), doodle.java2d.examples.PulsingCircle.animation.take(112)).unsafeRunSync()
  // println("Saved")
}
