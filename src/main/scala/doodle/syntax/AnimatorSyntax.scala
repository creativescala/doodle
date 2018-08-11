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
package syntax

import cats.{Monoid,Traverse}
import doodle.algebra.Image
import doodle.engine.{Animator,Engine }
import scala.concurrent.SyncVar

trait AnimatorSyntax {
  implicit class AnimatorOps[Algebra,F[_],A,T[_]](frames: T[Image[Algebra,F,A]]) {
    def animate[C](canvas: C)(implicit a: Animator[C], e: Engine[Algebra, F, C], m: Monoid[A], t: Traverse[T]): A = {
      val frame = new SyncVar[Image[Algebra,F,A]]
      val result = new SyncVar[A]

      val cancel =
        a.onFrame(canvas){
          val ioa = e.render(canvas){ algebra => frame.take().apply(algebra) }
          ioa.map(a => result.put(a)).unsafeRunSync()
        }

      val answer =
        t.foldLeft(frames, m.empty){(accum, image) =>
          frame.put(image)
          m.combine(accum, result.take())
        }
      cancel()
      answer
    }
  }
}
