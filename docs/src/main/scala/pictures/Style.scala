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

package docs
package pictures

import cats.effect.unsafe.implicits.global
import doodle.core._
import doodle.java2d._
import doodle.syntax.all._

object Style {
  val basicStyle =
    Picture
      .circle(100)
      .beside(
        Picture
          .circle(100)
          .strokeColor(Color.purple)
          .strokeWidth(5.0)
          .fillColor(Color.lavender)
      )

  basicStyle.save("pictures/basic-style.png")

  val strokeStyle =
    Picture
      .star(5, 50, 25)
      .strokeWidth(5.0)
      .strokeColor(Color.limeGreen)
      .strokeJoin(Join.bevel)
      .strokeCap(Cap.round)
      .strokeDash(Array(9.0, 7.0))
      .beside(
        Picture
          .star(5, 50, 25)
          .strokeWidth(5.0)
          .strokeColor(Color.greenYellow)
          .strokeJoin(Join.miter)
          .strokeCap(Cap.square)
          .strokeDash(Array(12.0, 9.0))
      )

  strokeStyle.save("pictures/stroke-style.png")

  val fillStyle =
    Picture
      .square(100)
      .fillGradient(
        Gradient.linear(
          Point(-50, 0),
          Point(50, 0),
          List((Color.red, 0.0), (Color.yellow, 1.0)),
          Gradient.CycleMethod.repeat
        )
      )
      .strokeWidth(5.0)
      .margin(0.0, 5.0, 0.0, 0.0)
      .beside(
        Picture
          .circle(100)
          .fillGradient(
            Gradient.dichromaticRadial(Color.limeGreen, Color.darkBlue, 50)
          )
          .strokeWidth(5.0)
      )

  fillStyle.save("pictures/fill-style.png")
}
