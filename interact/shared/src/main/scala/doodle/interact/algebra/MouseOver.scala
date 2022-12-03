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
package interact
package algebra

import doodle.algebra.Algebra
import fs2.Pure
import fs2.Stream

/** Algebra for elements that can respond to Mouseover events */
trait MouseOver extends Algebra {

  /** Attaches a mouse over event listener to the given img. The stream produces
    * an event every time the mouseOver event fires.
    */
  def mouseOver[A](img: Drawing[A]): (Drawing[A], Stream[Pure, Unit])
}
