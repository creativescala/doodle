/*
 * Copyright 2015 Creative Scala
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

import cats.instances.list.*
import doodle.core.*
import doodle.image.syntax.all.*
import doodle.syntax.all.*

object Stars {
  def star(sides: Int, skip: Int, radius: Double) = {
    import PathElement.*

    val centerAngle = 360.degrees * skip.toDouble / sides.toDouble

    val elements = (0 to sides) map { index =>
      val pt = Point.polar(radius, centerAngle * index.toDouble)
      if index == 0 then moveTo(pt)
      else lineTo(pt)
    }

    Image
      .path(OpenPath(elements.toList))
      .strokeWidth(2)
      .strokeColor(Color.hsl(centerAngle, 1, .25))
      .fillColor(Color.hsl(centerAngle, 1, .75))
  }

  val image =
    ((3 to 33 by 2) map { sides =>
      ((1 to sides / 2) map { skip =>
        star(sides, skip, 20)
      }).toList.allBeside
    }).toList.allAbove
}
