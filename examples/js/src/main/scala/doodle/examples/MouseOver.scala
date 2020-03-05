package doodle
package examples

import doodle.core._
import doodle.svg._
import doodle.syntax._
import doodle.interact.syntax._
import monix.reactive.Observable

object MouseOver {
  val initialColor = Color.royalBlue

  def coloredCircle(color: Color): Picture[Unit] =
      circle(300).fillColor(color)

  def makeFrames(color: Color): Observable[Picture[Unit]] = {
    println("makeFrames")
    val p1 = coloredCircle(color)

    val (p2, obs) = p1.mouseOver

    // Observable(p2) ++ obs.map{_ => println(s"gooey kablooie"); coloredCircle(initialColor.spin(15.degrees))}
    Observable.cons(p2,
                    // makeFrames(color.spin(15.degrees)))
                    obs.flatMap(_ => makeFrames(color.spin(15.degrees))))
    // obs.map(_ => coloredCircle(initialColor.spin(15.degrees))))
    // obs.map{a => println(s"snooped on $a"); a}.flatMap{_ => println("mouse move invoked"); makeFrames(color.spin(10.degrees))})
  }

  val frames = makeFrames(initialColor)

  val frame = Frame("canvas").size(600, 600)
  //frames.animate(canvas)
}
