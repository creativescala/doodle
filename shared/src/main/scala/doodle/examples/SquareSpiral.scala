package doodle
package examples

import doodle.core._
import doodle.syntax._
import doodle.turtle._

object SquareSpiral {
  import Instruction._

  def iterate(steps: Int, distance: Double, angle: Angle, increment: Double): List[Instruction] = {
    steps match {
      case 0 => Nil
      case n => forward(distance) :: turn(angle) :: iterate(steps-1, distance + increment, angle, increment)
    }
  }

  def squareSpiral(steps: Int): List[Instruction] =
    iterate(steps, 2, 89.5.degrees, 2)

  val instructions = squareSpiral(200)
  val image = Turtle.draw(instructions)
}
