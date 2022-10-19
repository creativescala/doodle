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

object Dash {
  import cats.instances.all._
  import doodle.core._
  import doodle.core.format.Gif
  import doodle.syntax.all._
  import doodle.java2d.effect._
  import doodle.interact.syntax.all._
  import fs2.Stream
  import cats.effect.IO
  import scala.concurrent.duration._
  import cats.effect.unsafe.implicits.global

  val frame = Frame.size(600, 600).background(Color.midnightBlue)
  val maxSize = 300
  val minSize = 50
  val increment = 10

  val dash: Array[Double] = Array(13, 8, 5, 3, 2, 1, 2, 3, 5, 8, 13)

  def curve(size: Double): Picture[Unit] =
    (ClosedPath.empty
      .moveTo(-size, -size)
      .curveTo(-size, size, -size, size, size, size)
      .curveTo(size, -size, size, -size, -size, -size))
      .path[Algebra]
      .strokeDash(dash)
      .strokeColor(Color.limeGreen)
      .strokeWidth(5.0)

  val animation: Stream[IO, Picture[Unit]] =
    Stream(1).repeat
      .debounce[IO](200.millis)
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
