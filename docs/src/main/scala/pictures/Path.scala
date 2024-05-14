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

package docs
package pictures

import cats.effect.unsafe.implicits.global
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

object Path {
  val path =
    ClosedPath.empty
      .lineTo(100, 100)
      .curveTo(90, 75, 90, 25, 10, 10)
      .moveTo(100, 100)
      .curveTo(75, 90, 25, 90, 10, 10)

  val picture = path.path.strokeWidth(3.0).strokeCap(Cap.round)

  picture.save("pictures/basic-path.png")

  val open =
    OpenPath.empty
      .curveTo(90, 0, 100, 10, 50, 50)
      .path
      .strokeColor(Color.red)
      .strokeWidth(3.0)

  val closed =
    ClosedPath.empty
      .curveTo(90, 0, 100, 10, 50, 50)
      .path
      .strokeColor(Color.blue)
      .strokeWidth(3.0)

  val paths = open.beside(closed)

  paths.save("pictures/open-closed-paths.png")

  val points =
    for (x <- 0.to(360)) yield Point(x, x.degrees.sin * 100)

  val curve = Picture.interpolatingSpline(points.toList)

  curve.save("pictures/curve.png")
}
