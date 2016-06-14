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
  final case object NoOp extends Instruction

  def forward(distance: Double): Instruction =
    Forward(distance)

  def turn(angle: Angle): Instruction =
    Turn(angle)

  def branch(instructions: Instruction*): Branch =
    Branch(instructions.toList)

  val noop: Instruction =
    NoOp
}
