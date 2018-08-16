/*
 * Copyright 2015 noelwelsh
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
package examples

import cats.instances.list._
import doodle.core._
import doodle.language.Basic
import doodle.syntax._

object Polygons {
  def image[F[_],A] = Basic.image{ implicit algebra: Basic[F] =>
    import algebra._

    def polygon(sides: Int, radius: Double) = {
      val centerAngle = 360.degrees / sides.toDouble

      val shape = (0 until sides).foldLeft(ClosedPath.empty){ (path, index) =>
        val point = Point.polar(radius, centerAngle * index.toDouble)
        if(index == 0) path.moveTo(point) else path.lineTo(point)
      }

      path(shape).strokeWidth(3).strokeColor(Color.hsl(centerAngle, 1, .5))
    }

    ((3 to 10) map (polygon(_, 200))).toList.allOn
  }
}
