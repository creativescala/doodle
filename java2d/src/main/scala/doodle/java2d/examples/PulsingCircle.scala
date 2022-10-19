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

object PulsingCircle {
  import cats.instances.all._
  import doodle.core._
  import doodle.core.format.Gif
  import doodle.syntax.all._
  import doodle.java2d.effect._
  import doodle.interact.syntax.all._
  import fs2.Stream
  import cats.effect.IO
  import cats.effect.unsafe.implicits.global

  val frame = Frame.size(600, 600).background(Color.midnightBlue)

  val strokeWidth = 9.0
  val gapWidth = 6.0
  val minimumDiameter = gapWidth + strokeWidth
  val maxNumberOfDisks = 15

  def disk(count: Int): Picture[Unit] =
    count match {
      case 0 =>
        Picture
          .circle(minimumDiameter.toDouble)
          .noFill
          .strokeWidth(strokeWidth)
      case n =>
        Picture
          .circle(
            (n * 2 * (strokeWidth + gapWidth) + minimumDiameter)
          )
          .noFill
          .strokeWidth(strokeWidth)
    }

  def background(count: Int): Picture[Unit] = {
    def iter(count: Int): Picture[Unit] =
      count match {
        case 0 =>
          disk(count)
        case n =>
          disk(count).on(iter(n - 1))
      }

    iter(count).strokeWidth(strokeWidth.toDouble).strokeColor(Color.darkGray)
  }

  def pulse(count: Int): Picture[Unit] =
    count match {
      case 0 => disk(0).strokeColor(Color.crimson)
      case 1 =>
        disk(1)
          .strokeColor(Color.crimson)
          .on(disk(0).strokeColor(Color.crimson.spin(30.degrees)))
      case n =>
        disk(n)
          .strokeColor(Color.crimson)
          .on(
            disk(n - 1)
              .strokeColor(Color.crimson.spin(30.degrees))
          )
          .on(
            disk(n - 2)
              .strokeColor(Color.crimson.spin(60.degrees))
          )
    }

  val animation: Stream[IO, Picture[Unit]] =
    Stream(1).repeat
      .scan((1, 0)) { (state, _) =>
        val (inc, count) = state
        if (count >= maxNumberOfDisks) (-1, maxNumberOfDisks - 1)
        else if (count <= 0) (1, 1)
        else (inc, count + inc)
      }
      .map { case (_, c) => pulse(c).on(background(maxNumberOfDisks)) }

  def go() =
    animation.animateFrames(frame)

  def write() =
    animation.take(100).write[Gif]("pulsing-circle-2.gif", frame)
}
