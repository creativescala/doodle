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

object Interact {
  import doodle.core._
  import doodle.syntax._
  import doodle.interact.syntax._
  import doodle.java2d.effect._
  import monix.reactive.Observable
  import monix.catnap.ConcurrentQueue
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
      Picture { implicit algebra =>
        algebra
          .circle(age.toDouble)
          .strokeColor(Color.hotpink.alpha(((maxAge - age) / (maxAge.toDouble)).normalized))
          .at(x, y)
      }
  }

  def ripples(canvas: Canvas): Observable[Picture[Unit]] = {
    import monix.eval.Task

    val queue =
      ConcurrentQueue[Task]
        .bounded[Option[Ripple]](5)
        .runSyncUnsafe()

    canvas
      .redraw
      .map(_ => none[Ripple])
      .mapEval(r => queue.offer(r))
      .subscribe()

    canvas
      .mouseMove
      .throttleFirst(FiniteDuration(100, MILLISECONDS)) // Stop spamming with too many mouse events
      .map(pt => Ripple(0, pt.x, pt.y).some)
      .mapEval(r => queue.offer(r))
      .subscribe()

    Observable
      .repeatEvalF(queue.poll)
      .scan(List.empty[Ripple]){(ripples, ripple) =>
        ripple match {
          case Some(r) => r :: ripples
          case None => ripples.filter(_.alive).map(_.older)
        }
      }
      .map(ripples => Picture{ implicit algebra =>
             ripples.map(_.picture(algebra)).allOn
           })
  }


  def go(): Unit = {
    // frame.canvas.flatMap(canvas => ripples(canvas.mouseMove).map(_.animateToCanvas(canvas))).unsafeRunSync()
    val canvas = frame.canvas.unsafeRunSync()
    ripples(canvas).animateToCanvas(canvas)
  }
}