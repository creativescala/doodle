package doodle.examples

import doodle.core._

object ChessBoard extends Drawable {
  val blackSquare = Rectangle(30, 30) fillColor Color.black
  val redSquare   = Rectangle(30, 30) fillColor Color.red

  val twoByTwo =
    (redSquare   beside blackSquare) above
    (blackSquare beside redSquare)

  val fourByFour =
    (twoByTwo beside twoByTwo) above
    (twoByTwo beside twoByTwo)

  val draw =
    (fourByFour beside fourByFour) above
    (fourByFour beside fourByFour)
}
