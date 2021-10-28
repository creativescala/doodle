package doodle
package interact
package examples

import doodle.algebra.Picture
import doodle.core._
import doodle.language.Basic
import doodle.syntax._
import fs2.Pure
import fs2.Stream

object Orbit {

  def planet[F[_]](angle: Angle): Picture[Basic, F, Unit] =
    circle[Basic, F](20)
      .fillColor(Color.brown.spin(angle))
      .at(Point(200, angle))

  def frames[F[_]]: Stream[Pure, Picture[Basic, F, Unit]] =
    Stream
      .range(0, 360, 1)
      .map(a => a.toDouble.degrees)
      .map(planet _)
}
