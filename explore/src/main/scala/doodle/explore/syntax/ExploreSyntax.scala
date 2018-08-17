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
import doodle.animate.Animator
import doodle.engine.{Engine,Frame}

trait ExploreSyntax {
  implicit class ExploreFunctionOps[A,Algebra,F[_],B](f: A => Image[Algebra,F,B]) {
    def explore[C](implicit ex: ExplorerFactory[_,A], a: Animator[C], e: Engine[Algebra,F,C], m: Monoid[B]): B = {
      (for {
          canvas <- e.frame(Frame.size(600, 600))
          values <- ex.create.render
          b      <- a.animateObservable(canvas)(values.map(f))
        } yield b).unsafeRunSync()
    }
  }
}
