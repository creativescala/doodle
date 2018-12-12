package doodle
package image
package examples

import doodle.core._
import doodle.image.Image
import doodle.random._
import cats.implicits._

object Layers {
  val point =
    (Random.double, Random.double).mapN((x, y) => Point(x * 800, y * 150))

  val radius = Random.natural(30)

  val circle =
    (point, radius).mapN((pt, r) => Image.circle(r.toDouble).at(pt.toVec))

  def layer = {
    List.range(0, 200).foldM(Image.empty) { (img, _) =>
      circle.map(_ on img)
    }
  }

  val cake =
    for {
      top <- layer
      mid <- layer
      bot <- layer
    } yield
      (top.noFill
        .strokeColor(Color.deepPink))
        .above(mid.noFill.strokeColor(Color.yellowGreen))
        .above(bot.noFill.strokeColor(Color.dodgerBlue))

  val image = cake.run
}
