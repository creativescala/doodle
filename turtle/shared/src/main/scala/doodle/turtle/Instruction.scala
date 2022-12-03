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

import doodle.core.Angle

sealed abstract class Instruction extends Product with Serializable
object Instruction {
  final case class Forward(distance: Double) extends Instruction
  final case class Turn(angle: Angle) extends Instruction
  final case class Branch(instructions: List[Instruction]) extends Instruction {
    def +:(instruction: Instruction): Branch =
      Branch(instruction +: instructions)

    def :+(instruction: Instruction): Branch =
      Branch(instructions :+ instruction)

    def ++(is: List[Instruction]): Branch =
      Branch(instructions ++ is)
  }
  case object NoOp extends Instruction

  def forward(distance: Double): Instruction =
    Forward(distance)

  def turn(angle: Angle): Instruction =
    Turn(angle)

  def branch(instructions: Instruction*): Branch =
    Branch(instructions.toList)

  val noop: Instruction =
    NoOp
}
