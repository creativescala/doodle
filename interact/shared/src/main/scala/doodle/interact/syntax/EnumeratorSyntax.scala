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
package interact
package syntax

import doodle.interact.animation._
import scala.concurrent.duration.Duration

trait EnumeratorSyntax {
  import EnumeratorSyntax._

  implicit class EnumeratorBuilderOps[A](start: A) {
    def upTo(end: A): HalfOpenEnumeratorBuilder[A] =
      HalfOpenEnumeratorBuilder(start, end)

    def upToIncluding(end: A): ClosedEnumeratorBuilder[A] =
      ClosedEnumeratorBuilder(start, end)
  }
}
object EnumeratorSyntax {
    final case class HalfOpenEnumeratorBuilder[A](start: A, end: A) {
    def forDuration(
        duration: Duration
    )(implicit e: Enumerator[A]): Transducer[A] = {
      this.forSteps((duration.toMillis * 60) / 1000)
    }
    def forSteps(steps: Long)(implicit e: Enumerator[A]): Transducer[A] =
      e.upto(start, end, steps)
  }

  final case class ClosedEnumeratorBuilder[A](start: A, end: A) {
    def forDuration(
        duration: Duration
    )(implicit e: Enumerator[A]): Transducer[A] = {
      this.forSteps((duration.toMillis * 60) / 1000)
    }
    def forSteps(steps: Long)(implicit e: Enumerator[A]): Transducer[A] =
      e.uptoIncluding(start, end, steps)
  }
}
