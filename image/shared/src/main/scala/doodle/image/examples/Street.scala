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
package image
package examples

import cats.instances.list._
import doodle.core._
import doodle.image.syntax.all._

object Street {
  import Color._

  val roof = Image.triangle(50, 30) fillColor brown

  val frontDoor =
    (Image.rectangle(50, 15) fillColor red) above (
      (Image.rectangle(10, 25) fillColor black) on
        (Image.rectangle(50, 25) fillColor red)
    )

  val house = roof above frontDoor

  val tree =
    (Image.circle(25) fillColor green) above
      (Image.rectangle(10, 20) fillColor brown)

  val street =
    (
      (0 to 105 by 45) map { _ =>
        (Image.rectangle(30, 3) fillColor yellow) beside
          (Image.rectangle(15, 3) fillColor black) above
          (Image.rectangle(45, 7) fillColor black)
      }
    ).toList.allBeside

  val houseAndGarden =
    (house beside tree) above street

  val image = (
    houseAndGarden beside
      houseAndGarden beside
      houseAndGarden
  ) strokeWidth 0
}
