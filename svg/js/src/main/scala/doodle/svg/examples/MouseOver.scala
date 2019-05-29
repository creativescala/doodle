package doodle
package svg
package examples

import doodle.core._
import doodle.svg._
import doodle.syntax._
import doodle.interact.syntax._
import monix.reactive.Observable

object MouseOver {
  val initialColor = Color.royalBlue

  def makeFrames(color: Color): Observable[Picture[Unit]] = {
    val p1 =
      Picture{ implicit algebra =>
        import algebra._

        circle(300).fillColor(color)
      }

    val (p2, obs) = p1.mouseOver

    Observable.cons(p2, obs.flatMap{_ => makeFrames(color.spin(10.degrees))})
  }

  val frames = makeFrames(initialColor)

  val frame = Frame("canvas").size(600,600)
  //frames.animate(canvas)
}
