/*
 * Copyright 2015 Noel Welsh
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

import cats.effect.IO
import doodle.core.Point
import doodle.interact.algebra.MouseClick
import fs2.Stream

trait MouseClickSyntax {
  implicit class MouseClickOps[Canvas](canvas: Canvas) {
    def mouseClick(implicit m: MouseClick[Canvas]): Stream[IO, Point] =
      m.mouseClick(canvas)
  }
}
