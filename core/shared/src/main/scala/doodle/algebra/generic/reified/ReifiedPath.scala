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
package algebra
package generic
package reified

import cats.data.Writer
import doodle.core._
import doodle.core.{Transform => Tx}

trait ReifiedPath extends GenericPath[Writer[List[Reified],?]] {
  object PathApi extends PathApi {
    def append(a: Option[Reified], b: Option[Reified]): Writer[List[Reified],Unit] =
      Writer.tell(a.toList ++ b.toList)

    def closedPath(tx: Tx, fill: Option[Fill], stroke: Option[Stroke], elements: List[PathElement]): Writer[List[Reified],Unit] =
      append(
        fill.map(f => Reified.fillClosedPath(tx, f, elements)),
        stroke.map(s => Reified.strokeClosedPath(tx, s, elements))
      )

    def openPath(tx: Tx, fill: Option[Fill], stroke: Option[Stroke], elements: List[PathElement]): Writer[List[Reified],Unit] =
      append(
        fill.map(f => Reified.fillOpenPath(tx, f, elements)),
        stroke.map(s => Reified.strokeOpenPath(tx, s, elements))
      )
  }
}
