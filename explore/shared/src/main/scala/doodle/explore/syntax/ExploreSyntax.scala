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
package explore
package syntax

import cats.Monoid
import doodle.algebra.{Algebra, Picture}
import doodle.effect.DefaultRenderer
import doodle.explore.effect.ExplorerFactory
import doodle.interact.effect.AnimationRenderer
import monix.execution.Scheduler

trait ExploreSyntax {
  implicit class ExploreFunctionOps[A, Alg[x[_]] <: Algebra[x], F[_], B](
      f: A => Picture[Alg, F, B]) {
    def explore[Frame, Canvas](frame: Frame)(
        implicit ex: ExplorerFactory[_, A],
        a: AnimationRenderer[Canvas],
        e: DefaultRenderer[Alg, F, Frame, Canvas],
        s: Scheduler,
        m: Monoid[B]): Unit = {
      (for {
        canvas <- e.canvas(frame)
        values <- ex.create.render
        b <- a.animate(canvas)(values.map(f))
      } yield b).unsafeRunAsync(r =>
        r match {
          case Left(err) =>
            println("There was an error rendering an animation")
            err.printStackTrace()

          case Right(_) => ()
      })
    }
  }
}
