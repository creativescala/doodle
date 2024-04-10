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
package svg

import cats.effect.unsafe.implicits.global
import doodle.core._
import doodle.svg._
import doodle.syntax.all._

import scala.scalajs.js.annotation._

@JSExportTopLevel("ConcentricCircles")
object ConcentricCircles {
  def circles(count: Int): Picture[Unit] =
    if (count == 0) Picture.circle(20).fillColor(Color.hsl(0.degrees, 0.7, 0.6))
    else
      Picture
        .circle(count.toDouble * 20.0)
        .fillColor(Color.hsl((count * 15).degrees, 0.7, 0.6))
        .under(circles(count - 1))

  @JSExport
  def draw(mount: String) =
    circles(7).drawWithFrame(Frame(mount))
}
