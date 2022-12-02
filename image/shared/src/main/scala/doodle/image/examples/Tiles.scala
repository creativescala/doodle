/*
 * Copyright 2015 Noel Welsh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package image
package examples

import cats.instances.all._
import cats.syntax.all._
import doodle.core._
import doodle.image.Image
import doodle.image.syntax.all._
import doodle.random._
import doodle.syntax.all._

object Tiles {

  import PathElement._

  // Experiments generating tiles

  def randomColor(meanHue: Angle): Random[Color] =
    for {
      hue <- Random.normal(meanHue.toDegrees, 10.0) map (_.degrees)
      sat <- Random.normal(0.8, 0.05)
      light <- Random.normal(0.6, 0.05)
      alpha <- Random.normal(0.5, 0.1)
    } yield Color.hsla(hue, sat, light, alpha)

  def randomTriangle(width: Double): Random[Image] = {
    val coord = Random.natural(width.floor.toInt).map(_.toDouble)
    val point = (coord, coord) mapN { (x, y) =>
      Point.cartesian(x, y)
    }
    for {
      pt1 <- point
      pt2 <- point
      pt3 <- point
    } yield Image.closedPath(
      Seq(moveTo(pt1), lineTo(pt2), lineTo(pt3), lineTo(pt1))
    )
  }

  val leafGreen: Random[Color] = randomColor(80.degrees)
  val aquamarine: Random[Color] = randomColor(160.degrees)

  def randomTile(n: Int, color: Random[Color]): Random[Image] =
    (1 to n).toList
      .map { _ =>
        (randomTriangle(100), color) mapN { _ fillColor _ }
      }
      .sequence
      .map(images => images.foldLeft(Image.empty) { _ on _ })

  def tile(baseN: Int, topN: Int): Random[Image] =
    (randomTile(baseN, aquamarine), randomTile(topN, leafGreen)) mapN {
      _ under _
    }

  def tileGrid(tile: Random[Image], sideLength: Int): Random[Image] = {
    val row: Random[Image] =
      (1 to sideLength).map(_ => tile).toList.sequence.map(_.allBeside)
    val grid: Random[Image] =
      (1 to sideLength).map(_ => row).toList.sequence.map(_.allAbove)
    grid
  }

  def image: Image = {
    tileGrid(tile(400, 20), 3).run
  }
}
