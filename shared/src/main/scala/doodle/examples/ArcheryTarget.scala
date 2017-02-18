package doodle.examples

import doodle.core._

object ArcheryTarget {
  val blackAndWhiteTarget =
    Image.circle(10) on Image.circle(20) on Image.circle(30)

  val coloredTarget =
    (
      Image.circle(10).fillColor(Color.red) on
        Image.circle(20).fillColor(Color.white) on
        Image.circle(30).fillColor(Color.red)
    )

  val stand =
    (Image.rectangle(6, 20) above Image.rectangle(20, 6)).fillColor(Color.brown)

  val ground =
    Image.rectangle(80, 25).lineWidth(0).fillColor(Color.green)

  val image = coloredTarget above stand above ground
}
