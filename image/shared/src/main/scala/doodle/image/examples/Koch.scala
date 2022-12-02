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
import doodle.core._
import doodle.image.Image
import doodle.image.syntax.all._
import doodle.syntax.all._

object Koch {
  import PathElement._

  def kochElements(
      depth: Int,
      start: Point,
      angle: Angle,
      length: Double
  ): Seq[PathElement] = {
    if (depth == 0) {
      Seq(lineTo(start + Vec.polar(length, angle)))
    } else {
      val lAngle = angle - 60.degrees
      val rAngle = angle + 60.degrees

      val third = length / 3.0
      val edge = Vec.polar(third, angle)

      val mid1 = start + edge
      val mid2 = mid1 + edge.rotate(-60.degrees)
      val mid3 = mid2 + edge.rotate(60.degrees)

      kochElements(depth - 1, start, angle, third) ++
        kochElements(depth - 1, mid1, lAngle, third) ++
        kochElements(depth - 1, mid2, rAngle, third) ++
        kochElements(depth - 1, mid3, angle, third)
    }
  }

  def koch(depth: Int, length: Double): Image = {
    val origin = Point.cartesian(0, length / 6)
    Image.openPath(
      moveTo(origin) +: kochElements(depth, origin, 0.degrees, length)
    )
  }

  val image = ((1 to 4)
    .map { depth =>
      koch(depth, 512)
    })
    .toList
    .allAbove
}
