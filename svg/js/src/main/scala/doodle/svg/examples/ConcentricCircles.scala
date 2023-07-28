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

package doodle.svg.examples

import cats.effect.IOApp
import doodle.core._
import doodle.svg._
import doodle.syntax.all._

object ConcentricCircles extends IOApp.Simple {
  def circles(count: Int): Picture[Unit] =
    if (count <= 0) Picture.empty
    else
      Picture
        .circle(count.toDouble * 20.0)
        .fillColor(Color.skyBlue.spin((count.toDouble * 10.0).degrees))
        .under(circles(count - 1))

  val run =
    for {
      canvas <- Frame("svg-root").canvas()
      a <- circles(10).drawWithCanvasToIO(canvas)
    } yield a
}
