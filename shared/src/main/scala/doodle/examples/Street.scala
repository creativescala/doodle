package doodle.examples

import doodle.core._

object Street {
  import Color._

  val roof = Image.triangle(50, 30) fillColor brown

  val frontDoor =
    (Image.rectangle(50, 15) fillColor red) above (
      (Image.rectangle(10, 25) fillColor black) on
      (Image.rectangle(50, 25) fillColor red)
    )

  val house = roof above frontDoor

  val tree =
    (Image.circle(25) fillColor green) above
    (Image.rectangle(10, 20) fillColor brown)

  val street =
    allBeside(
      (0 to 105 by 45) map { i =>
        (Image.rectangle(30, 3) fillColor yellow) beside
        (Image.rectangle(15, 3) fillColor black) above
        (Image.rectangle(45, 7) fillColor black)
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
