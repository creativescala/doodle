/*
 * Copyright 2015-2020 Noel Welsh
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
package java2d
package examples

object Explore {
  import doodle.core._
  import doodle.syntax._

  import doodle.explore.syntax._

  import cats.instances.all._

  val wave = (period: Double, amplitude: Double, color: Color) =>
      (-300 to 300).toList.map { x =>
        val y = Math.sin(x / period) * amplitude
        circle[Algebra,Drawing](10)
          .fillColor(color)
          .at(x.toDouble, y)
      }.allOn

  def draw(): Unit = {
    wave(50, 300, Color.cornflowerBlue).draw()
  }

  def explore(): Unit = {
    wave.tupled.explore(Frame.size(600, 600))
  }
}
