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
package golden

import doodle.core.Coordinate
import doodle.core.Landmark
import doodle.image.*
import munit.*

class ImageLayout extends FunSuite with GoldenImage {
  testImage("layout-at-debug") {
    val c = Image.circle(20)

    c.debug
      .beside(c.debug.at(5, 5))
      .beside(c.debug.at(5, -5))
      .above(c.debug.beside(c.at(5, 5).debug).beside(c.at(5, -5).debug))
      .above(c.beside(c.at(5, 5)).beside(c.at(5, -5)).debug)
  }

  testImage("image-landmarks") {
    Image
      .circle(100)
      .originAt(Landmark(Coordinate.percent(50), Coordinate.percent(-50)))
      .on(
        Image
          .circle(100)
          .originAt(Landmark(Coordinate.percent(-50), Coordinate.percent(-50)))
      )
      .on(
        Image
          .circle(100)
          .originAt(Landmark(Coordinate.percent(-50), Coordinate.percent(50)))
      )
      .on(
        Image
          .circle(100)
          .originAt(Landmark(Coordinate.percent(50), Coordinate.percent(50)))
      )
  }

  testImage("image-size") {
    Image.circle(100).size(50, 50).beside(Image.square(50)).size(150, 150)
  }
}
