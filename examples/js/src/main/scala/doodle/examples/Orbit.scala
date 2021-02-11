package doodle
package examples

import doodle.core._
import doodle.syntax._
import doodle.svg._
import monix.reactive.Observable

object Orbit {

  def planet(angle: Angle): Picture[Unit] =
    circle[Algebra, Drawing](20)
      .fillColor(Color.brown.spin(angle))
      .at(Point(200, angle))

  val background =
    circle[Algebra, Drawing](400)
      .strokeDash(Array(5.0, 5.0))
      .strokeColor(Color.midnightBlue)

  val frames =
    Observable
      .repeat(1)
      .scan(0) { (angle, inc) =>
        if (angle >= 360) 0 + inc
        else angle + inc
      }
      .map(a => planet(a.toDouble.degrees))
}
