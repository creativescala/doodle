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

import cats.implicits._
import doodle.core._
import doodle.image.Image
import doodle.random._

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
    } yield (top.noFill
      .strokeColor(Color.deepPink))
      .above(mid.noFill.strokeColor(Color.yellowGreen))
      .above(bot.noFill.strokeColor(Color.dodgerBlue))

  val image = cake.run
}
