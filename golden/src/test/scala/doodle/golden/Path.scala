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
package golden

import cats.implicits._
import doodle.java2d._
import doodle.syntax.all._
import munit._

class Path extends FunSuite with GoldenPicture {

  testPicture("path-polygons") {
    regularPolygon[Algebra](4, 100)
      .beside(regularPolygon[Algebra](5, 100))
      .beside(regularPolygon[Algebra](7, 100))
      .beside(regularPolygon[Algebra](20, 100))
  }

  testPicture("path-stars") {
    star[Algebra](4, 100, 50)
      .beside(star[Algebra](5, 100, 50))
      .beside(star[Algebra](7, 100, 50))
      .beside(star[Algebra](20, 100, 50))
  }

  testPicture("path-rounded-rectangle") {
    roundedRectangle[Algebra](100, 50, 10)
      .beside(roundedRectangle[Algebra](100, 100, 15))
      .beside(roundedRectangle[Algebra](50, 100, 20))
      .beside(roundedRectangle[Algebra](100, 100, 0))
  }

  testPicture("path-equilateral-triangle") {
    equilateralTriangle[Algebra](100)
      .beside(equilateralTriangle[Algebra](150))
      .beside(equilateralTriangle[Algebra](50))
      .beside(equilateralTriangle[Algebra](200))
  }
}
