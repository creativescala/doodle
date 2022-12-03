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

import doodle.core._
import doodle.image.Image

object OpenClosedPaths {
  import Point._
  import PathElement._

  val openCurve =
    Image.openPath(
      List(curveTo(cartesian(50, 100), cartesian(100, 100), cartesian(150, 0)))
    )

  val closedCurve = openCurve.close

  val openTriangle =
    Image.openPath(
      List(
        lineTo(cartesian(50, 100)),
        lineTo(cartesian(100, 0)),
        lineTo(cartesian(0, 0))
      )
    )

  val closedTriangle = openTriangle.close

  def dropShadow(image: Image): Image =
    image
      .strokeColor(Color.cornflowerBlue)
      .strokeWidth(10.0)
      .on(image.strokeColor(Color.black).strokeWidth(10.0).at(10, 10))

  val image =
    dropShadow(openCurve)
      .beside(dropShadow(closedCurve))
      .above(dropShadow(openTriangle).beside(dropShadow(closedTriangle)))
}
