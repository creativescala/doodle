package doodle
package turtle
package examples

import doodle.core._
import doodle.image._
import doodle.syntax._

object CreativeScala {
  object turtle {
    import doodle.turtle._
    import doodle.turtle.Instruction._

    val instructions =
      List(
        forward(100),
        turn(90.degrees),
        forward(100),
        turn(90.degrees),
        forward(100),
        turn(90.degrees),
        forward(100)
      )

    val square = Turtle.draw(instructions)

    def polygon(sides: Int, sideLength: Double): Image = {
      val rotation = Angle.one / sides.toDouble
      def iter(n: Int): List[Instruction] =
        n match {
          case 0 => Nil
          case n => turn(rotation) +: forward(sideLength) +: iter(n - 1)
        }

      Turtle.draw(iter(sides))
    }
  }

  object branching {
    import doodle.turtle._
    import doodle.turtle.Instruction._

    val y = List(
      forward(100),
      branch(turn(45.degrees), forward(100)),
      branch(turn(-45.degrees), forward(100))
    )

    val yImage = Turtle.draw(y)

    val bud = branch(
      turn(45.degrees),
      forward(5),
      turn(-90.degrees),
      forward(5),
      turn(-90.degrees),
      forward(5),
      turn(-90.degrees),
      forward(5)
    )

    val f = forward(20)

    val b1 = branch(turn(45.degrees), f)
    val b2 = branch(turn(-45.degrees), f)

    val treeOne = List(f, bud)
    val treeTwo = List(f, f, b1 :+ (bud), b2 :+ (bud))
    val treeThree = List(
      f,
      f,
      f,
      f,
      b1 ++ (List(f, b1 :+ bud, b2 :+ (bud))),
      b2 ++ (List(f, b1 :+ bud, b2 :+ (bud)))
    )

    val spacer = Image.rectangle(10, 10).noFill.noStroke

    val branches =
      Turtle.draw(treeOne) beside spacer beside Turtle.draw(
        treeTwo
      ) beside spacer beside Turtle
        .draw(treeThree) strokeColor Color.forestGreen

    val stepSize = 10

    def rule(i: Instruction): List[Instruction] =
      i match {
        case Forward(_) =>
          List(forward(stepSize.toDouble), forward(stepSize.toDouble))
        case NoOp =>
          List(
            branch(turn(45.degrees), forward(stepSize.toDouble), noop),
            branch(turn(-45.degrees), forward(stepSize.toDouble), noop)
          )
        case other => List(other)
      }

    def branching =
      List
        .tabulate(5) { n =>
          LSystem.iterate(n, List(forward(stepSize.toDouble), noop), rule)
        }
        .map { is =>
          Turtle.draw(is)
        }
        .foldLeft(Image.empty) { _ beside _ }
  }
}
