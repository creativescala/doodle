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

import doodle.core.Color
import doodle.java2d._
import doodle.syntax.all._
import munit._

class FrameBackground extends FunSuite with GoldenPicture {
  testPictureWithFrame("black-background")(
    Frame.default.withSizedToPicture().withBackground(Color.black)
  ) {
    circle[Algebra](20).fillColor(Color.white)
  }

  testPictureWithFrame("red-background")(
    Frame.default.withSizedToPicture().withBackground(Color.red)
  ) {
    circle[Algebra](20).fillColor(Color.white)
  }
}
