package doodle
package turtle

import doodle.core._

object Turtle {
  final case class State(at: Vec, heading: Angle)

  def draw(instructions: List[Instruction], angle: Angle = Angle.zero): Image = {
    import Instruction._
    import PathElement._

    val initialState = State(Vec.zero, angle)

    def iterate(state: State, instructions: List[Instruction]): List[PathElement] = {
      val (_, path)=
        instructions.foldLeft( (state, List.empty[PathElement]) ){ (accum, elt) =>
          val (state, path) = accum
          elt match {
            case Forward(d) =>
              val nowAt = state.at + Vec.polar(state.heading, d)
              val element = lineTo(nowAt.toPoint)

              (state.copy(at = nowAt), element :: path)
            case Turn(a) =>
              val nowHeading = state.heading + a

              (state.copy(heading = nowHeading), path)
            case Branch(i) =>
              val branchedPath = iterate(state, i)

              (state, MoveTo(state.at.toPoint) :: (branchedPath ++ path))
            case NoOp =>
              accum
          }
        }

      path
    }

    Image.openPath(moveTo(0, 0) :: iterate(initialState, instructions).reverse)
  }
}

