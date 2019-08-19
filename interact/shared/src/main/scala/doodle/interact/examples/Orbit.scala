package doodle
package interact
package examples

import doodle.language.Basic
import doodle.algebra.Picture
import doodle.core._
import doodle.syntax._
import monix.reactive.Observable

object Orbit {

  def planet[F[_]](angle: Angle): Picture[Basic, F, Unit] =
    circle[Basic, F](20).fillColor(Color.brown.spin(angle)).at(Point(200, angle))

  def frames[F[_]]: Observable[Picture[Basic, F, Unit]] =
    Observable
      .range(0, 360, 1)
      .map(a => a.toDouble.degrees)
      .map(planet _)
}
