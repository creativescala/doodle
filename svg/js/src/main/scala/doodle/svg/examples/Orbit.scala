package doodle
package svg
package examples

import monix.reactive.Observable

object Orbit {
  import doodle.core._
  import doodle.syntax._
  import doodle.language.Basic
  import doodle.svg._

  def planet(angle: Angle) =
    Basic.picture[Drawing, Unit]{ implicit algebra: Basic[Drawing] =>
      import algebra._

      circle(20).fillColor(Color.brown.spin(angle)).at(Point(200, angle))
    }

  val frames =
    Observable.range(0, 360, 1)
      .map(a => a.toDouble.degrees)
      .map(planet _)
}
