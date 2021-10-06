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

object Ripples {
  import cats.effect.unsafe.implicits.global
  import cats.effect.IO
  import doodle.core._
  import doodle.syntax._
  import doodle.interact.syntax._
  import doodle.java2d.effect._
  import fs2.Stream
  import cats.effect.IO
  import scala.concurrent.duration.{FiniteDuration, MILLISECONDS}

  import cats.instances.all._
  import cats.syntax.all._

  val frame = Frame.size(600, 600).background(Color.midnightBlue)

  final case class Ripple(age: Int, x: Double, y: Double) {
    val maxAge = 200
    def alive: Boolean = age <= maxAge

    def older: Ripple =
      this.copy(age = age + 1)

    def picture: Picture[Unit] =
      circle[Algebra, Drawing](age.toDouble)
        .strokeColor(
          Color.hotpink.alpha(((maxAge - age) / (maxAge.toDouble)).normalized)
        )
        .at(x, y)
  }

  def ripples(canvas: Canvas): IO[Stream[IO, Picture[Unit]]] = {
    import cats.effect.std.Queue
    Queue
      .bounded[IO, Option[Ripple]](5)
      .flatMap { queue =>
        val redraw = canvas.redraw
          .map(_ => none[Ripple])
          .evalMap(r => queue.offer(r))
          .compile
          .drain

        val mouseMove = canvas.mouseMove
          .debounce(
            FiniteDuration(100, MILLISECONDS)
          ) // Stop spamming with too many mouse events
          .map(pt => Ripple(0, pt.x, pt.y).some)
          .evalMap(r => queue.offer(r))
          .compile
          .drain

        val ripples: Stream[IO, Picture[Unit]] = Stream
          .fromQueueUnterminated(queue)
          .scan(List.empty[Ripple]) { (ripples, ripple) =>
            ripple match {
              case Some(r) => r :: ripples
              case None    => ripples.filter(_.alive).map(_.older)
            }
          }
          .map(ripples => ripples.map(_.picture).allOn)
        (redraw.start >> mouseMove.start).as(ripples)
      }
  }

  def go(): Unit = {
    // frame.canvas.flatMap(canvas => ripples(canvas.mouseMove).map(_.animateToCanvas(canvas))).unsafeRunSync()
    (for {
      canvas <- frame.canvas()
      frames <- ripples(canvas)
      a <- frames.animateWithCanvasToIO(canvas)
    } yield a).unsafeRunAsync(println _)
  }
}
