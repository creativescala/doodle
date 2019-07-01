package doodle
package svg
package examples

import doodle.core._
import doodle.syntax._
import doodle.language.Basic
import doodle.svg._
import monix.reactive.Observable

object Orbit {

  def planet(angle: Angle) =
    Basic.picture[Drawing, Unit]{ implicit algebra: Basic[Drawing] =>
      import algebra._

      circle(20).fillColor(Color.brown.spin(angle)).at(Point(200, angle))
    }

  val frames =
    Observable
      .repeat(1)
      .scan(0){ (angle, inc) =>
        if(angle >= 360) 0 + inc
        else angle + inc
      }
      .map(a => planet(a.toDouble.degrees))
}
