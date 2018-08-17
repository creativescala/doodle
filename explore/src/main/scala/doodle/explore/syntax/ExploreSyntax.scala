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
package explore
package syntax

import cats.Monoid
import doodle.algebra.Image
import doodle.engine.{Animator,Engine,Frame}
import monix.execution._
import monix.reactive._
import monix.reactive.subjects.ConcurrentSubject
// import scala.concurrent.SyncVar

trait ExploreSyntax {
  implicit class ExploreFunctionOps[A,Algebra,F[_],B](f: A => Image[Algebra,F,B]) {
    def explore[C](implicit ex: ExplorerFactory[_,A], a: Animator[C], e: Engine[Algebra,F,C], m: Monoid[B]): Unit = {
      implicit val scheduler = monix.execution.Scheduler.fixedPool("Explorer animation", 4)

      // val frame = new SyncVar[Image[Algebra,F,B]]
      // val result = new SyncVar[B]

      val canvas = e.frame(Frame.size(600, 600)).unsafeRunSync()

      val explorer = ex.create
      val value = explorer.render.unsafeRunSync()

      val frames: ConcurrentSubject[Unit,Unit] =
        ConcurrentSubject.publish(new OverflowStrategy.DropOld(2))

      val cancel: () => Unit =
        a.onFrame(canvas){
          // println("Frame requested")
          frames.onNext(()) match {
            case Ack.Continue => ()
            case Ack.Stop => ()
          }
          // println("Frame request published")
        }

      value
        .combineLatest(frames)
        .map{ case(r, _) => r }
        .map(f)
        .foldLeftF(m.empty){(accum, image) =>
          val result = e.render(canvas)(algebra => image(algebra)).unsafeRunSync()
          m.combine(accum, result)
        }
        .runAsyncGetFirst
        .onComplete(_ => cancel())

      ()
    }
  }
}
