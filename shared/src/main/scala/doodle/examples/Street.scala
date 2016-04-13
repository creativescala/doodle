package doodle.examples

import doodle.core._
import doodle.syntax._

object Street {
  import Color._

  val roof = Triangle(50, 30) fillColor brown

  val frontDoor =
    (Rectangle(50, 15) fillColor red) above (
      (Rectangle(10, 25) fillColor black) on
      (Rectangle(50, 25) fillColor red)
    )

  val house = roof above frontDoor

  val tree =
    (Circle(25) fillColor green) above
    (Rectangle(10, 20) fillColor brown)

  val street =
    allBeside(
      (0 to 105 by 45) map { i =>
        (Rectangle(30, 3) fillColor yellow) beside
        (Rectangle(15, 3) fillColor black) above
        (Rectangle(45, 7) fillColor black)
      }
    )


  val houseAndGarden =
    (house beside tree) above street

  val image = (
    houseAndGarden beside
    houseAndGarden beside
    houseAndGarden
  ) lineWidth 0
}
