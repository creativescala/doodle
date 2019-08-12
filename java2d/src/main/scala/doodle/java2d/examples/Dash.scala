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

object Dash {
  import cats.instances.all._
  import doodle.core._
  import doodle.effect.Writer.Gif
  import doodle.syntax._
  import doodle.java2d.effect._
  import doodle.interact.syntax._
  import monix.reactive.Observable
  import scala.concurrent.duration._

  val frame = Frame.size(600, 600).background(Color.midnightBlue)
  val maxSize = 300
  val minSize = 50
  val increment = 10

  val dash: Array[Double] = Array(13, 8, 5, 3, 2, 1, 2, 3, 5, 8, 13)

  def curve(size: Double): Picture[Unit] =
    Picture{ implicit algebra =>
      algebra.path(ClosedPath
                     .empty
                     .moveTo(-size, -size)
                     .curveTo(-size, size, -size, size, size, size)
                     .curveTo(size, -size, size, -size, -size, -size))
        .strokeDash(dash)
        .strokeColor(Color.limeGreen)
        .strokeWidth(5.0)
    }

  val animation: Observable[Picture[Unit]] =
    Observable
      .repeat(1)
      .sample(200.millis)
      .scan((1, 0)) { (state, _) =>
        val (inc, size) = state
        if (size >= maxSize) (-increment, maxSize - increment)
        else if (size <= minSize) (increment, minSize + increment)
        else (inc, size + inc)
      }
      .map { case (_, s) => curve(s.toDouble) }

  def go() =
    animation.animateFrames(frame)

  def write() =
    animation.take(100).write[Gif]("dash.gif", frame)
}
