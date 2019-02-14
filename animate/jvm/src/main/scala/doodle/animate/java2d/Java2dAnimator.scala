/*
 * Copyright 2015 noelwelsh
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
package animate
package java2d

import cats.Monoid
import cats.effect.IO
import doodle.algebra.Image
import doodle.effect.Renderer
import doodle.java2d.effect.Java2DFrame
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.{Consumer, Observable}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object Java2dAnimator extends Animator[Java2DFrame] {
  val frameRate = 16.milliseconds

  def animateIterable[Algebra, F[_], A, Frame](canvas: Java2DFrame)(
      frames: Iterable[Image[Algebra, F, A]])(
      implicit e: Renderer[Algebra, F, Frame, Java2DFrame],
      m: Monoid[A]): IO[A] =
    animateObservable(canvas)(
      Observable.fromIterable(frames).delayOnNext(frameRate))

  def animateObservable[Algebra, F[_], A, Frame](canvas: Java2DFrame)(
      frames: Observable[Image[Algebra, F, A]])(
      implicit e: Renderer[Algebra, F, Frame, Java2DFrame],
      m: Monoid[A]): IO[A] = {
    frames
      .sampleRepeated(frameRate)
      .mapEval(img => Task.fromIO(e.render(canvas)(algebra => img(algebra))))
      .consumeWith(Consumer.foldLeft(m.empty) { (accum, a) =>
        m.combine(accum, a)
      })
      .toIO(
        Task.catsEffect(Scheduler(canvas.timer, ExecutionContext.fromExecutor(canvas.timer)))
      )
  }
}
