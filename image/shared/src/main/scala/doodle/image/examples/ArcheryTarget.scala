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

object ArcheryTarget {
  val blackAndWhiteTarget =
    Image.circle(10) on Image.circle(20) on Image.circle(30)

  val coloredTarget =
    (
      Image.circle(10).fillColor(Color.red) on
        Image.circle(20).fillColor(Color.white) on
        Image.circle(30).fillColor(Color.red)
    )

  val stand =
    (Image.rectangle(6, 20) above Image.rectangle(20, 6)).fillColor(Color.brown)

  val ground =
    Image.rectangle(80, 25).strokeWidth(0).fillColor(Color.green)

  val image = coloredTarget above stand above ground
}
