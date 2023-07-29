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
import doodle.core._
import doodle.java2d._
import doodle.syntax.all._

object Size {
  val circle =
    Picture
      .circle(100)
      .strokeColor(Color.midnightBlue)

  val circleBoundingBox: Picture[BoundingBox] =
    circle.boundingBox

  val boundingBox =
    circleBoundingBox.flatMap(bb =>
      Picture
        .roundedRectangle(bb.width, bb.height, 3.0)
        .strokeColor(Color.hotpink)
        .strokeWidth(3.0)
    )

  circle.on(boundingBox).save("pictures/bounding-box.png")
}
