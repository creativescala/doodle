package doodle
package turtle
package examples

import doodle.core._
import doodle.syntax.all._
import doodle.turtle.Instruction._
import doodle.turtle._

object Geometry {
  val instructions =
    List(
      forward(100),
      turn(45.degrees),
      forward(100),
      turn(45.degrees),
      forward(100),
      turn(45.degrees),
      forward(100),
      turn(45.degrees),
      forward(100),
      turn(45.degrees),
      forward(100),
      turn(45.degrees),
      forward(100),
      turn(45.degrees),
      forward(100)
    )

  val image = Turtle.draw(instructions).fillColor(Color.red)
}
