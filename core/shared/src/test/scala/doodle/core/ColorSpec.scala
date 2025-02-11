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

package doodle.core

import doodle.core.Color.Oklch
import doodle.core.Color.Rgb
import munit.ScalaCheckSuite
import org.scalacheck.Prop.*

class ColorSpec extends ScalaCheckSuite {
  property(".toRgb andThen .toOklch is the identity") {
    forAll(Generators.oklch) { (oklch: Oklch) =>
      (oklch ~= (oklch.toRgb.toOklch))
    }
  }

  property(".toOklch andThen .toRgb is the identity") {
    forAll(Generators.rgb) { (rgb: Rgb) =>
      (rgb ~= (rgb.toOklch.toRgb))
    }
  }
}
