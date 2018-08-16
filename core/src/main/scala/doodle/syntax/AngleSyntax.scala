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
package syntax

import doodle.core.Angle

trait AngleSyntax {
  implicit class AngleDoubleOps(val angle: Double) {
    def degrees: Angle =
      Angle.degrees(angle)

    def radians: Angle =
      Angle.radians(angle)

    def turns: Angle =
      Angle.turns(angle)
  }

  implicit class AngleIntOps(val angle: Int) {
    def degrees: Angle =
      Angle.degrees(angle.toDouble)

    def radians: Angle =
      Angle.radians(angle.toDouble)

    def turns: Angle =
      Angle.turns(angle.toDouble)
  }
}
