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
import doodle.syntax.all.*

object RainbowSierpinski {
  def sierpinski(size: Double, color: Color): Image = {
    if size > 8 then {
      val delta = 120.degrees * (size / 512.0)
      sierpinski(size / 2, color.spin(delta)) above (
        sierpinski(size / 2, color.spin(delta * 2)) beside
          sierpinski(size / 2, color.spin(delta * 3))
      )
    } else {
      Image.triangle(size, size).strokeWidth(0).fillColor(color)
    }
  }

  def image = sierpinski(512, Color.red)
}
