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

import org.scalacheck.Prop._
import org.scalacheck._

class TransformSpec extends Properties("Transform") {
  import doodle.arbitrary._
  import doodle.syntax.approximatelyEqual._

  property("scale scale the x and y coordinates appropriately") = forAll {
    (scale: Scale, point: Point) =>
      val expected = Point.cartesian(point.x * scale.x, point.y * scale.y)
      Transform.scale(scale.x, scale.y)(point) ~= expected
  }

  property("rotate rotate the point") = forAll {
    (rotate: Rotate, point: Point) =>
      val expected = point.rotate(rotate.angle)
      Transform.rotate(rotate.angle)(point) ~= expected
  }

  property("andThen compose the transformations") = forAll {
    (translate: Translate, rotate: Rotate, point: Point) =>
      val expected =
        Point
          .cartesian(point.x + translate.x, point.y + translate.y)
          .rotate(rotate.angle)
      val tx =
        Transform
          .translate(translate.x, translate.y)
          .andThen(Transform.rotate(rotate.angle))

      tx(point) ~= expected
  }

  property("horizontalReflection") = forAll { (point: Point) =>
    Transform.horizontalReflection(point) ?= Point(-point.x, point.y)
  }

  property("verticalReflection") = forAll { (point: Point) =>
    Transform.verticalReflection(point) ?= Point(point.x, -point.y)
  }

  property("logicalToScreen") = forAll { (screen: Screen, point: Point) =>
    val expected = Point(
      point.x + (screen.width.toDouble / 2.0),
      (screen.height.toDouble / 2.0) - point.y
    )
    val tx =
      Transform.logicalToScreen(screen.width.toDouble, screen.height.toDouble)
    val actual = tx(point)

    (actual ~= expected) :| s"actual: $actual\nexpected: $expected"
  }

  property("screenToLogical") = forAll { (screen: Screen, point: Point) =>
    val expected = Point(
      point.x - (screen.width.toDouble / 2.0),
      (screen.height.toDouble / 2.0) - point.y
    )
    val tx =
      Transform.screenToLogical(screen.width.toDouble, screen.height.toDouble)
    val actual = tx(point)

    (actual ~= expected) :| s"actual: $actual\nexpected: $expected"
  }
}
