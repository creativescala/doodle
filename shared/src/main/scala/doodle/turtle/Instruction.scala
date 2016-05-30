package doodle
package turtle

import doodle.core.Angle

sealed abstract class Instruction extends Product with Serializable
object Instruction {
  final case class Forward(distance: Double) extends Instruction
  final case class Turn(angle: Angle) extends Instruction
  final case class Branch(instructions: List[Instruction]) extends Instruction
  final case object NoOp extends Instruction

  def forward(distance: Double): Instruction =
    Forward(distance)

  def turn(angle: Angle): Instruction =
    Turn(angle)

  def branch(instructions: List[Instruction]): Instruction =
    Branch(instructions)

  val noop: Instruction =
    NoOp
}
