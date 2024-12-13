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
package java2d
package examples

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.syntax.all.*
import doodle.core.*
import doodle.interact.syntax.all.*
import doodle.java2d.effect.*
import doodle.syntax.all.*
import fs2.Stream

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.MILLISECONDS

object Ripples extends IOApp {

  val frame =
    Frame.default
      .withSize(600, 600)
      .withCenterAtOrigin
      .withBackground(Color.midnightBlue)

  final case class Ripple(age: Int, x: Double, y: Double) {
    val maxAge = 200
    def alive: Boolean = age <= maxAge

    def older: Ripple =
      this.copy(age = age + 1)

    def picture: Picture[Unit] =
      circle(age.toDouble)
        .strokeColor(
          Color.hotpink.alpha(((maxAge - age) / (maxAge.toDouble)).normalized)
        )
        .at(x, y)
  }

  def ripples(canvas: Canvas): IO[Stream[IO, Picture[Unit]]] = {
    import cats.effect.std.Queue
    Queue
      .bounded[IO, Option[Ripple]](5)
      .map { queue =>
        val redraw: Stream[IO, Unit] =
          canvas.redraw
            .map(_ => none[Ripple])
            .evalMap(r => queue.offer(r))

        val mouseMove: Stream[IO, Unit] =
          canvas.mouseMove
            .debounce(
              FiniteDuration(100, MILLISECONDS)
            ) // Stop spamming with too many mouse events
            .map(pt => Ripple(0, pt.x, pt.y).some)
            .evalMap(r => queue.offer(r))

        val ripples: Stream[IO, Picture[Unit]] =
          Stream
            .fromQueueUnterminated(queue)
            .scan(List.empty[Ripple]) { (ripples, ripple) =>
              ripple match {
                case Some(r) => r :: ripples
                case None    => ripples.filter(_.alive).map(_.older)
              }
            }
            .map(ripples => ripples.map(_.picture).allOn)

        redraw.drain
          .merge(mouseMove.drain)
          .merge(ripples)
          .interruptWhen(canvas.closed.attempt)
      }
  }

  val go: IO[Unit] = {
    // frame.canvas.flatMap(canvas => ripples(canvas.mouseMove).map(_.animateToCanvas(canvas))).unsafeRunSync()
    frame
      .canvas()
      .use(canvas =>
        for {
          frames <- ripples(canvas)
          a <- frames.animateWithCanvasToIO(canvas)
        } yield a
      )
  }

  def run(args: List[String]): IO[ExitCode] =
    go.as(cats.effect.ExitCode.Success)
}
