/*
 * Copyright 2015 Noel Welsh
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
package core

import doodle.syntax.approximatelyEqual._
import org.scalacheck.Prop._
import org.scalacheck._

object CoordinateSpec extends Properties("Coordinate properties") {
  val smallNumber = Gen.choose(-300.0, 300.0)

  property("oneHundredPercent evaluates correctly") = forAll { (edge: Int) =>
    val actualEdge = Math.abs(edge)
    Coordinate.oneHundredPercent.eval(
      -actualEdge.toDouble,
      actualEdge.toDouble
    ) ?= actualEdge.toDouble
  }

  property("minusOneHundredPercent evaluates correctly") = forAll {
    (edge: Int) =>
      val actualEdge = Math.abs(edge)
      Coordinate.minusOneHundredPercent.eval(
        -actualEdge.toDouble,
        actualEdge.toDouble
      ) ?= -actualEdge.toDouble
  }

  property("addition of percent and point is correct") =
    forAllNoShrink(smallNumber, smallNumber) { (percent, point) =>
      val coord = Coordinate.percent(percent) + Coordinate.point(point)
      val pt = coord.eval(-200.0, 200.0)
      val expected = point + ((percent / 100.0) * 200)

      (expected ~= pt) :| s"percent $percent point $point, expected $expected, actual $pt"
    }

  property("subtraction of percent and point is correct") =
    forAllNoShrink(smallNumber, smallNumber) { (percent, point) =>
      val coord = Coordinate.point(point) - Coordinate.percent(percent)
      val pt = coord.eval(-200.0, 200.0)
      val expected = point - ((percent / 100.0) * 200)

      (expected ~= pt) :| s"percent $percent point $point, expected $expected, actual $pt"
    }
}
