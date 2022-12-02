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

import cats.instances.list._
import doodle.core._
import doodle.image.Image
import doodle.image.syntax.all._
import doodle.syntax.all._

object Polygon {

  def polygon(sides: Int, radius: Double) = {
    import PathElement._

    val centerAngle = 360.degrees / sides.toDouble

    val elements = (0 until sides) map { index =>
      val point = Point.polar(radius, centerAngle * index.toDouble)
      if (index == 0) moveTo(point) else lineTo(point)
    }

    Image
      .closedPath(elements)
      .strokeWidth(5)
      .strokeColor(Color.hsl(centerAngle, 1.0, .5))
  }

  def image = ((3 to 20) map (polygon(_, 100))).toList.allOn
}
