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
package image
package examples

import doodle.core._
import doodle.image.Image
import doodle.syntax.all._

object SierpinskiRipple {
  def triangle(size: Double, color: Color): Image = {
    Image.equilateralTriangle(size).fillColor(color).strokeColor(color)
  }

  def sierpinski(n: Int, size: Double, color: Color): Image = {
    if (n == 1) {
      triangle(size, color)
    } else {
      sierpinski(n - 1, size / 2, color.spin(-10.degrees))
        .above(
          sierpinski(n - 1, size / 2, color.spin(37.degrees))
            .beside(sierpinski(n - 1, size / 2, color.spin(79.degrees)))
        )
    }
  }

  val image = sierpinski(10, 1024, Color.brown)
}
