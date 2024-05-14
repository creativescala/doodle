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

import cats.implicits.*
import doodle.java2d.*
import doodle.syntax.all.*
import munit.*

class Layout extends FunSuite with GoldenPicture {

  testPicture("layout-size") {
    Picture
      .regularPolygon(4, 100)
      .size(50, 50)
      .beside(regularPolygon[Algebra](5, 100))
      .size(300, 300)
  }
}
