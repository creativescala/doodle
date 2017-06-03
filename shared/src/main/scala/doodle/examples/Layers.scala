package doodle
package examples

import doodle.core._
import doodle.random._
import cats.implicits._

object Layers {
  val point =
    (Random.double |@| Random.double).map((x, y) => Point(x * 800, y * 150))

  val radius = Random.natural(30)

  val circle = (point |@| radius).map((pt, r) => Image.circle(r).at(pt.toVec))

  def layer = {
    List.range(0, 200).foldM(Image.empty){ (img, _) =>
      circle.map(_ on img)
    }
  }

  val cake =
    for {
      top <- layer
      mid <- layer
      bot <- layer
    } yield (top.noFill.lineColor(Color.deepPink)).
      above(mid.noFill.lineColor(Color.yellowGreen)).
      above(bot.noFill.lineColor(Color.dodgerBlue))

  val image = cake.run
}
