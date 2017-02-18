package doodle
package turtle

import doodle.core._

object Turtle {
  final case class State(at: Vec, heading: Angle)

  def draw(instructions: List[Instruction], angle: Angle = Angle.zero): Image = {
    import Instruction._
    import PathElement._

    val initialState = State(Vec.zero, angle)

    // Note that iterate returns the path in *reversed* order.
    def iterate(state: State, instructions: List[Instruction]): (State, List[PathElement]) = {
      instructions.foldLeft( (state, List.empty[PathElement]) ){ (accum, elt) =>
        val (state, path) = accum
        elt match {
          case Forward(d) =>
            val nowAt = state.at + Vec.polar(d, state.heading)
            val element = lineTo(nowAt.toPoint)

            (state.copy(at = nowAt), element +: path)
          case Turn(a) =>
            val nowHeading = state.heading + a

            (state.copy(heading = nowHeading), path)
          case Branch(i) =>
            val (_, branchedPath) = iterate(state, i)

            (state, MoveTo(state.at.toPoint) +: (branchedPath ++ path))

          case NoOp =>
            accum
        }
      }
    }

    val (_, path) = iterate(initialState, instructions)
    Image.openPath(moveTo(0, 0) :: path.reverse.toList)
  }
}

