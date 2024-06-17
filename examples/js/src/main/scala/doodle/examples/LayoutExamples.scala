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

package doodle.examples

import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

/** All the examples from the Layout documentation page, written in a backend
  * independent style.
  */
trait LayoutExamples[Alg <: Debug & Layout & Path & Shape & Style]
    extends BaseExamples[Alg] {

  val basicLayout: Picture[Alg, Unit] =
    circle(100)
      .strokeColor(Color.blue)
      .beside(square(100).strokeColor(Color.darkBlue))
      .above(triangle(100, 100).strokeColor(Color.crimson))
      .strokeWidth(5.0)

  val debugLayout: Picture[Alg, Unit] =
    circle(100).debug
      .beside(regularPolygon(5, 30).debug)
      .above(
        circle(100).beside(regularPolygon(5, 30)).debug
      )

  val atAndOriginAt =
    circle(100)
      .at(25, 25)
      .debug
      .beside(circle(100).originAt(25, 25).debug)

  val pentagon: Picture[Alg, Unit] =
    circle(10)
      .at(50, 0.degrees)
      .on(circle(10).at(50, 72.degrees))
      .on(circle(10).at(50, 144.degrees))
      .on(circle(10).at(50, 216.degrees))
      .on(circle(10).at(50, 288.degrees))

  val overlappingCircles: Picture[Alg, Unit] =
    circle(100)
      .originAt(Landmark(Coordinate.percent(50), Coordinate.percent(-50)))
      .on(
        circle(100)
          .originAt(Landmark(Coordinate.percent(-50), Coordinate.percent(-50)))
      )
      .on(
        circle(100)
          .originAt(Landmark(Coordinate.percent(-50), Coordinate.percent(50)))
      )
      .on(
        circle(100)
          .originAt(Landmark(Coordinate.percent(50), Coordinate.percent(50)))
      )

  val rollingCirclesSize: Picture[Alg, Unit] =
    circle(50)
      .size(100, 25)
      .debug
      .beside(circle(50).size(80, 20).debug)
      .beside(circle(50).size(50, 15).debug)
      .beside(circle(50).size(20, 10).debug)
      .beside(circle(50).size(0, 0).debug)

  val rollingCirclesMargin: Picture[Alg, Unit] =
    circle(50)
      .margin(25)
      .debug
      .beside(circle(50).margin(15).debug)
      .beside(circle(50).debug)
      .beside(circle(50).margin(-15).debug)
      .beside(circle(50).margin(-25).debug)

  // If you add a new example, also add it in here
  val allPictures =
    List(
      basicLayout,
      debugLayout,
      atAndOriginAt,
      pentagon,
      overlappingCircles,
      rollingCirclesSize,
      rollingCirclesMargin
    )
}
