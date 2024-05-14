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

import doodle.core.*
import doodle.core.font.*

import Image.*

object BoxesAndArrows {
  val size = 100.0

  val spacer = rectangle(size * 0.2, size).noFill.noStroke

  val box =
    roundedRectangle(size, size, size * .12).strokeWidth(size * .12).noFill

  val font = Font(
    FontFamily.sansSerif,
    FontStyle.normal,
    FontWeight.normal,
    FontSize.points((size / 2.0).toInt)
  )
  val equals = text("=").font(font)

  val c = circle(size * 0.3).fillColor(Color.black)
  val t = triangle(size * 0.6, size * 0.6).fillColor(Color.black)

  val circleBox = box on c
  val triangleBox = box on t
  val circleAndTriangleBox =
    box on (circle(size * 0.15) beside triangle(size * 0.3, size * 0.3))

  val circleToTriangle =
    c beside spacer beside rightArrow(size, size).fillColor(
      Color.black
    ) beside spacer beside t

  val circleToTriangleBox =
    c beside spacer beside rightArrow(size, size).fillColor(
      Color.black
    ) beside spacer beside triangleBox

  def besideWithSpace(elts: List[Image]): Image =
    elts.foldLeft(Image.empty) { (accum, elt) =>
      accum beside spacer beside elt
    }

  val map: Image =
    besideWithSpace(
      List(
        circleBox,
        text("map").font(font),
        circleToTriangle,
        equals,
        triangleBox
      )
    )

  val applicative: Image =
    besideWithSpace(
      List(
        circleBox,
        text("|@|").font(font),
        triangleBox,
        equals,
        circleAndTriangleBox
      )
    )

  val flatMap: Image =
    besideWithSpace(
      List(
        circleBox,
        text("flatMap").font(font),
        circleToTriangleBox,
        equals,
        triangleBox
      )
    )
}
