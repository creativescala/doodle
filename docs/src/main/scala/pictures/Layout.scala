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

object Layout {
  val basicLayout =
    Picture
      .circle(100)
      .strokeColor(Color.blue)
      .beside(Picture.square(100).strokeColor(Color.darkBlue))
      .above(Picture.triangle(100, 100).strokeColor(Color.crimson))
      .strokeWidth(5.0)

  basicLayout.save("pictures/basic-layout.png")

  val debugLayout =
    Picture
      .circle(100)
      .debug
      .beside(Picture.regularPolygon(5, 30).debug)
      .above(
        Picture.circle(100).beside(Picture.regularPolygon(5, 30)).debug
      )

  debugLayout.save("pictures/debug-layout.png")

  val atAndOriginAt =
    Picture
      .circle(100)
      .at(25, 25)
      .debug
      .beside(Picture.circle(100).originAt(25, 25).debug)

  atAndOriginAt.save("pictures/at-and-origin-at.png")

  val pentagon =
    Picture
      .circle(10)
      .at(50, 0.degrees)
      .on(Picture.circle(10).at(50, 72.degrees))
      .on(Picture.circle(10).at(50, 144.degrees))
      .on(Picture.circle(10).at(50, 216.degrees))
      .on(Picture.circle(10).at(50, 288.degrees))

  pentagon.save("pictures/pentagon.png")

  val overlappingCircles =
    Picture
      .circle(100)
      .originAt(Landmark(Coordinate.percent(50), Coordinate.percent(-50)))
      .on(
        Picture
          .circle(100)
          .originAt(Landmark(Coordinate.percent(-50), Coordinate.percent(-50)))
      )
      .on(
        Picture
          .circle(100)
          .originAt(Landmark(Coordinate.percent(-50), Coordinate.percent(50)))
      )
      .on(
        Picture
          .circle(100)
          .originAt(Landmark(Coordinate.percent(50), Coordinate.percent(50)))
      )

  overlappingCircles.save("pictures/overlapping-circles.png")

  val circle = Picture.circle(50)
  val rollingCircles =
    circle
      .margin(25)
      .beside(circle.margin(15))
      .beside(circle)
      .beside(circle.margin(-15))
      .beside(circle.margin(-25))
      // Increase the bounding box so it covers the whole image.
      // Otherwise the image is cropped when it is rendered.
      .size(300, 100)

  rollingCircles.save("pictures/rolling-circles.png")

  val rollingCirclesMargin =
    circle
      .margin(25)
      .debug
      .beside(circle.margin(15).debug)
      .beside(circle.debug)
      .beside(circle.margin(-15).debug)
      .beside(circle.margin(-25).debug)
      // Increase the bounding box so it covers the whole image.
      // Otherwise the image is cropped when it is rendered.
      .size(300, 100)

  rollingCirclesMargin.save("pictures/rolling-circles-margin.png")

  // Rolling circles using the size method
  val rollingCirclesSize =
    circle
      .size(100, 25)
      .debug
      .beside(circle.size(80, 20).debug)
      .beside(circle.size(50, 15).debug)
      .beside(circle.size(20, 10).debug)
      .beside(circle.size(0, 0).debug)
      .size(300, 100)

  rollingCirclesSize.save("pictures/rolling-circles-size.png")
}
