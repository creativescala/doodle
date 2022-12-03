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
package turtle

import doodle.core._
import doodle.image.Image

object Turtle {
  final case class State(at: Vec, heading: Angle)

  def draw(
      instructions: List[Instruction],
      angle: Angle = Angle.zero
  ): Image = {
    import Instruction._
    import PathElement._

    val initialState = State(Vec.zero, angle)

    // Note that iterate returns the path in *reversed* order.
    def iterate(
        state: State,
        instructions: List[Instruction]
    ): (State, List[PathElement]) = {
      instructions.foldLeft((state, List.empty[PathElement])) { (accum, elt) =>
        val (state, path) = accum
        elt match {
          case Forward(d) =>
            val nowAt = state.at + Vec.polar(d, state.heading)
            val element = lineTo(nowAt.toPoint)

            (state.copy(at = nowAt), element +: path)
          case Turn(a) =>
            val nowHeading = state.heading + a

            (state.copy(heading = nowHeading), path)
          case Branch(i) =>
            val (_, branchedPath) = iterate(state, i)

            (state, MoveTo(state.at.toPoint) +: (branchedPath ++ path))

          case NoOp =>
            accum
        }
      }
    }

    val (_, path) = iterate(initialState, instructions)
    Image.openPath(moveTo(0, 0) :: path.reverse.toList)
  }
}
