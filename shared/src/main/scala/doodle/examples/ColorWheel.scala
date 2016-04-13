package doodle.examples

import doodle.core._
import doodle.syntax._

object ColorWheel {
  val blobs = for {
    l <- (0 to 100 by 10) map (_ / 100.0)
    h <- (0 to 360 by 10)
    r  = 200 * l
    a  = Angle.degrees(h)
  } yield {
    Circle(20).
      at(r * a.sin, r * a.cos).
      lineWidth(0).
      fillColor(Color.hsl(a, 1.normalized, l.normalized))
  }

  def image = blobs.reduceLeft(_ on _)
}
