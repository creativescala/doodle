import doodle.core._
import doodle.syntax._
import doodle.turtle._
import doodle.turtle.Instruction._

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
