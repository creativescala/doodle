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
package interact
package easing

import doodle.syntax.approximatelyEqual._
import org.scalacheck.Prop._
import org.scalacheck._

object EasingSpec extends Properties("Easing properties") {
  property("identity is the identity") = forAllNoShrink { (t: Double) =>
    Easing.identity(t) =? t
  }

  property("followedBy sends [0,0.5) to first function") =
    forAllNoShrink(Gen.chooseNum(0.0, 0.5).filter(_ != 0.5)) { (t: Double) =>
      val f = Easing.identity.followedBy(Easing.quadratic)
      f(t) =? t && (t >= 0.0) && (t <= 0.5)
    }

  property("followedBy sends [0.5,1.0] to second function") =
    forAllNoShrink(Gen.chooseNum(0.5, 1.0)) { (t: Double) =>
      val f = Easing.quadratic.followedBy(Easing.identity)
      (f(t) >= 0.5) && (f(t) <= 1.0)
    }

  property("reflect is its own inverse") =
    forAllNoShrink(Gen.chooseNum(0.0, 1.0)) { (t: Double) =>
      Easing.identity(t) ?= Easing.identity.reflect.reflect(t)
    }

  property("starts at 0") = (Easing.linear(0.0) ~= 0.0) &&
    (Easing.quadratic(0.0) ~= 0.0) &&
    (Easing.cubic(0.0) ~= 0.0) &&
    (Easing.sin(0.0) ~= 0.0) &&
    (Easing.circle(0.0) ~= 0.0) &&
    (Easing.back(0.0) ~= 0.0)

  property("ends at 1") = (Easing.linear(1.0) ~= 1.0) &&
    (Easing.quadratic(1.0) ~= 1.0) &&
    (Easing.cubic(1.0) ~= 1.0) &&
    (Easing.sin(1.0) ~= 1.0) &&
    (Easing.circle(1.0) ~= 1.0) &&
    (Easing.back(1.0) ~= 1.0)
}
