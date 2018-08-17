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
package syntax

import cats.Monoid
import doodle.algebra.Image
import doodle.engine.Engine
import monix.reactive.Observable

trait AnimateSyntax {
  implicit class AnimateIterableOps[Algebra,F[_],A](frames: Iterable[Image[Algebra,F,A]]) {
    def animate[C](canvas: C)(implicit a: Animator[C], e: Engine[Algebra, F, C], m: Monoid[A]): A = {
      a.animateIterable(canvas)(frames).unsafeRunSync()
    }
  }

  implicit class AnimateObservableOps[Algebra,F[_],A](frames: Observable[Image[Algebra,F,A]]) {
    def animate[C](canvas: C)(implicit a: Animator[C], e: Engine[Algebra, F, C], m: Monoid[A]): A = {
      a.animateObservable(canvas)(frames).unsafeRunSync()
    }
  }

}
