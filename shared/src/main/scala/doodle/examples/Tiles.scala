package doodle
package examples

import doodle.core._
import doodle.syntax._
import doodle.random._

import cats.instances.list._
import cats.syntax.cartesian._
import cats.syntax.traverse._

object Tiles {
  import PathElement._

  // Experiments generating tiles

  def randomColor(meanHue: Angle): Random[Color] =
    for {
      hue   <- Random.normal(meanHue.toDegrees, 10.0) map (_.degrees)
      sat   <- Random.normal(0.8, 0.05) map (_.normalized)
      light <- Random.normal(0.6, 0.05) map (_.normalized)
      alpha <- Random.normal(0.5, 0.1) map (_.normalized)
    } yield Color.hsla(hue, sat, light, alpha)

  def randomTriangle(width: Double): Random[Image] = {
    val coord = Random.natural(width.floor.toInt)
    val point = (coord |@| coord) map { (x, y) => Point.cartesian(x, y) }
    for {
      pt1 <- point
      pt2 <- point
      pt3 <- point
    } yield Image.closedPath(Seq(moveTo(pt1), lineTo(pt2), lineTo(pt3), lineTo(pt1)))
  }

  val leafGreen: Random[Color] = randomColor(80.degrees)
  val aquamarine: Random[Color] = randomColor(160.degrees)

  def randomTile(n: Int, color: Random[Color]): Random[Image] =
    (1 to n).toList.map{ _ =>
      (randomTriangle(100) |@| color) map { _ fillColor _ }
    }.sequenceU.map(images => images.foldLeft(Image.empty){ _ on _ })

  def tile(baseN: Int, topN: Int): Random[Image] =
    (randomTile(baseN, aquamarine) |@| randomTile(topN, leafGreen)) map { _ under _ }

  def tileGrid(tile: Random[Image], sideLength: Int): Random[Image] = {
    val row: Random[Image] =
      (1 to sideLength).map(_ => tile).toList.sequenceU.map(images =>
        images.foldLeft(Image.empty){ (row, img) => row beside img }
      )
    val grid: Random[Image] =
      (1 to sideLength).map(_ => row).toList.sequenceU.map(rows =>
        rows.foldLeft(Image.empty){ (grid, row) => grid above row }
      )
    grid
  }

  def image: Image ={
    tileGrid(tile(400, 20), 3).run
  }
}
