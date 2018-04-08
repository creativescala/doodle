/*
 * Copyright 2015 noelwelsh
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
package algebra

import doodle.core.Point

object Transform {
  type Transform = Point => Point

  def logicalToScreen(width: Double, height: Double): Transform =
    (logical: Point) => {
      val x = logical.x
      val y = logical.y

      Point(x + (width / 2), height / 2 - y)
    }

  def screenToLogical(width: Double, height: Double): Transform =
    (screen: Point) => {
      val x = screen.x
      val y = screen.y

      Point(x - (width / 2), height / 2 - y)
    }
}
