package doodle
package interact
package examples

import doodle.algebra.Picture
import doodle.core._
import doodle.language.Basic
import doodle.syntax.all._
import fs2.Pure
import fs2.Stream

object Orbit {

  def planet(angle: Angle): Picture[Basic, Unit] =
    circle[Basic](20)
      .fillColor(Color.brown.spin(angle))
      .at(Point(200, angle))

  def frames: Stream[Pure, Picture[Basic, Unit]] =
    Stream
      .range(0, 360, 1)
      .map(a => a.toDouble.degrees)
      .map(planet _)
}
