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

object Layout {
  // Examples for debugging layout

  import doodle.core.Point.*
  import doodle.core.PathElement.*

  def addOrigin(image: Image): Image = {
    val origin = Image.circle(5).noStroke.fillColor(Color.red)
    origin on image
  }

  def boundingBox(w: Double, h: Double, at: Vec) = {
    Image.rectangle(w, h).strokeColor(Color.red).noFill.at(at)
  }

  // Examples of paths that are not centered in their bounding box
  val triangle =
    addOrigin(
      Image.path(
        OpenPath(
          List(
            lineTo(cartesian(50, 100)),
            lineTo(cartesian(100, 0)),
            lineTo(cartesian(0, 0))
          )
        )
      )
    ).on(boundingBox(100, 100, Vec(50, 50)))

  val curve =
    addOrigin(
      Image.path(
        OpenPath(
          List(
            curveTo(cartesian(50, 100), cartesian(100, 100), cartesian(150, 0))
          )
        )
      )
    ).on(boundingBox(150, 100, Vec(75, 50)))

  val vertical = triangle above (addOrigin(Image.circle(200))
    .on(boundingBox(200, 200, Vec.zero)))
  val horizontal = triangle beside (addOrigin(Image.circle(200))
    .on(boundingBox(200, 200, Vec.zero)))
}
