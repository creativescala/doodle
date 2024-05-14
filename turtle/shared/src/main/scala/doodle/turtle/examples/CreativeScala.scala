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
package examples

import doodle.core.*
import doodle.image.*
import doodle.syntax.all.*

object CreativeScala {
  object turtle {
    import doodle.turtle.*
    import doodle.turtle.Instruction.*

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
    import doodle.turtle.*
    import doodle.turtle.Instruction.*

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
