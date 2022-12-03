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

import doodle.core._

object ColorWheel {
  val blobs = for {
    l <- (0 to 100 by 10) map (_ / 100.0)
    h <- (0 to 360 by 10)
    r = 200 * l
    a = Angle.degrees(h.toDouble)
  } yield {
    Image
      .circle(20)
      .at(r * a.sin, r * a.cos)
      .strokeWidth(0)
      .fillColor(Color.hsl(a, 1.0, l))
  }

  def image = blobs.reduceLeft(_ on _)
}
