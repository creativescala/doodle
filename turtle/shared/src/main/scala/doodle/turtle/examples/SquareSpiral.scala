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
package turtle
package examples

import doodle.core._
import doodle.syntax.all._
import doodle.turtle._

object SquareSpiral {
  import Instruction._

  def iterate(
      steps: Int,
      distance: Double,
      angle: Angle,
      increment: Double
  ): List[Instruction] = {
    steps match {
      case 0 => Nil
      case _ =>
        forward(distance) :: turn(angle) :: iterate(
          steps - 1,
          distance + increment,
          angle,
          increment
        )
    }
  }

  def squareSpiral(steps: Int): List[Instruction] =
    iterate(steps, 2, 89.5.degrees, 2)

  val instructions = squareSpiral(200)
  val image = Turtle.draw(instructions)
}
