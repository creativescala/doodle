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

import doodle.core._
import doodle.image.Image
import doodle.syntax.all._
import doodle.turtle._

object LSystem {
  import Instruction._

  def iterate(
      steps: Int,
      seed: List[Instruction],
      rule: Instruction => List[Instruction]
  ): List[Instruction] = {
    def rewrite(instructions: List[Instruction]): List[Instruction] =
      instructions.flatMap {
        case Branch(i) =>
          List(branch(rewrite(i): _*))
        case other =>
          rule(other)
      }

    steps match {
      case 0 =>
        seed
      case _ =>
        val rewritten = rewrite(seed)
        iterate(steps - 1, rewritten, rule)
    }
  }

  object tree {
    val left = turn(25.degrees)
    val right = turn(-25.degrees)
    val f = forward(5)

    val rule = (i: Instruction) => {
      i match {
        case NoOp => //  F−[+X]+F[+FX]−X
          List(
            f,
            left,
            branch(branch(noop), right, noop),
            right,
            f,
            branch(right, f, noop),
            left,
            noop
          )

        case Forward(_) =>
          List(f, f)

        case other =>
          List(other)
      }
    }

    val image = Turtle
      .draw(iterate(6, List(noop), rule), 50.degrees)
      .strokeColor(Color.forestGreen)
  }

  object flowers {
    val f = forward(5)
    val spin = turn(51.degrees)
    val spur = List(spin, branch(f, noop))

    val rule = (i: Instruction) => {
      i match {
        case Forward(_) => List(f, f, f)
        case NoOp       => spur ++ spur ++ spur ++ spur ++ spur ++ spur ++ spur
        case other      => List(other)
      }
    }

    val image = Turtle.draw(iterate(5, List(noop), rule))
  }

  object kochCurve {
    val f = forward(5)
    val tP = turn(45.degrees)
    val tM = turn(-90.degrees)

    val spacer = Image.rectangle(5, 5).noFill.noStroke

    def rule(i: Instruction): List[Instruction] =
      i match {
        case Forward(_) => List(f, tP, f, tM, f, tP, f)
        case other      => List(other)
      }

    val seed = List(f)

    val iterations = List.tabulate(5) { n =>
      Turtle.draw(iterate(n, seed, rule))
    }

    val image =
      iterations.fold(Image.empty) { (img, i) =>
        img above spacer above i
      }
  }
}
