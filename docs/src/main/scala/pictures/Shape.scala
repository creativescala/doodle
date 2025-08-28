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
import doodle.core.*
import doodle.java2d.*
import doodle.syntax.all.*

object Shape {
  val basicShapes =
    Picture
      .circle(200)
      .strokeColor(Color.blue)
      .on(Picture.square(125).strokeColor(Color.darkBlue))
      .on(Picture.triangle(100, 100).strokeColor(Color.crimson))
      .strokeWidth(5.0)

  basicShapes.save("pictures/basic-shapes.png")

  def polygon(size: Int): Picture[Unit] =
    Picture
      .regularPolygon(5, size)
      .strokeColor(Tailwind4Colors.amber400)

  def star(size: Int): Picture[Unit] =
    Picture
      .star(5, size, size / 2)
      .strokeColor(Tailwind4Colors.sky500)

  val complexShapes =
    polygon(60)
      .on(star(70))
      .on(star(90))
      .on(polygon(80))
      .on(polygon(100))
      .on(star(110))
      .strokeWidth(7.0)

  complexShapes.save("pictures/complex-shapes.png")
}
