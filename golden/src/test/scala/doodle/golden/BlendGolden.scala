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
import doodle.java2d.* // Ensure Java2D backend is used for golden testing
import munit.*

class BlendGolden extends FunSuite with GoldenPicture {

  // A simple background and foreground for testing blend modes
  // Apply the Algebra type parameter directly to the shape function
  val background = circle[Algebra](100).fillColor(Color.red) // Changed from Picture.circle
  val foreground = circle[Algebra](80).fillColor(Color.blue)  // Changed from Picture.circle

  testPicture("blend-screen") {
    background.on(foreground.screen)
  }

  testPicture("blend-burn") {
    background.on(foreground.burn)
  }

  testPicture("blend-dodge") {
    background.on(foreground.dodge)
  }

  testPicture("blend-lighten") {
    background.on(foreground.lighten)
  }

  testPicture("blend-source-over") {
    background.on(foreground.sourceOver)
  }
}