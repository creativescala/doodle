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
package interact
package examples

import doodle.algebra.Picture
import doodle.core._
import doodle.language.Basic
import doodle.syntax.all._
import fs2.Pure
import fs2.Stream

object Orbit {

  def planet(angle: Angle): Picture[Basic, Unit] =
    circle[Basic](20)
      .fillColor(Color.brown.spin(angle))
      .at(Point(200, angle))

  def frames: Stream[Pure, Picture[Basic, Unit]] =
    Stream
      .range(0, 360, 1)
      .map(a => a.toDouble.degrees)
      .map(planet _)
}
