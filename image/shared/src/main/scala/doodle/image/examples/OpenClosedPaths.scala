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

import doodle.core.*

object OpenClosedPaths {
  val openCurve = OpenPath.empty.curveTo(
    Point(50, 100),
    Point(100, 100),
    Point(150, 0)
  )

  val closedCurve = openCurve.close

  val openTriangle =
    OpenPath.empty
      .lineTo(Point(50, 100))
      .lineTo(Point(100, 0))
      .lineTo(Point(0, 0))

  val closedTriangle = openTriangle.close

  def dropShadow(image: Image): Image =
    image
      .strokeColor(Color.cornflowerBlue)
      .strokeWidth(10.0)
      .on(image.strokeColor(Color.black).strokeWidth(10.0).at(10, 10))

  val image =
    dropShadow(Image.path(openCurve))
      .beside(dropShadow(Image.path(closedCurve)))
      .above(
        dropShadow(Image.path(openTriangle))
          .beside(dropShadow(Image.path(closedTriangle)))
      )
}
