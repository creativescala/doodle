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

object Shape {
  val basicShapes =
    Picture
      .circle(100)
      .strokeColor(Color.aqua)
      .beside(Picture.triangle(100, 100).strokeColor(Color.blue))
      .beside(Picture.square(100).strokeColor(Color.darkBlue))

  basicShapes.save("pictures/basic-shapes.png")
}
