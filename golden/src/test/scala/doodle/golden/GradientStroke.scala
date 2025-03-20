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

import doodle.core.*
import doodle.syntax.all.*
import doodle.java2d.*
import munit.*

class GradientStroke extends FunSuite with GoldenPicture {

  testPicture("linear-gradient-stroke") {
    Picture
      .square(200)
      .strokeGradient(
        Gradient.linear(
          Point(-100, -100),
          Point(100, 100),
          List(
            (Color.red, 0.0),
            (Color.yellow, 0.5),
            (Color.blue, 1.0)
          ),
          Gradient.CycleMethod.repeat
        )
      )
      .strokeWidth(20)
      .noFill
  }

  testPicture("radial-gradient-stroke") {
    Picture
      .circle(100)
      .strokeGradient(
        Gradient.radial(
          Point(0, 0),
          Point(0, 0),
          100,
          List(
            (Color.magenta, 0.3),
            (Color.cyan, 1.0)
          ),
          Gradient.CycleMethod.noCycle
        )
      )
      .strokeWidth(20)
      .noFill
  }
}
