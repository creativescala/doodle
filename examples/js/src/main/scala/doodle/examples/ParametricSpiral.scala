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

package doodle.examples

import cats.effect.unsafe.implicits.global
import doodle.algebra.*
import doodle.core.*
import doodle.syntax.all.*

trait ParametricSpiral[Alg <: Layout & Shape & Style] {

  def parametricSpiral(angle: Angle): Point =
    Point((Math.exp(angle.toTurns) - 1) * 200, angle)

  def drawCurve(
      points: Int,
      marker: Point => Picture[Alg, Unit],
      curve: Angle => Point
  ): Picture[Alg, Unit] = {
    // Angle.one is one complete turn. I.e. 360 degrees
    val turn = Angle.one / points.toDouble
    def loop(count: Int): Picture[Alg, Unit] = {
      count match {
        case 0 =>
          val pt = curve(Angle.zero)
          marker(pt).at(pt)
        case n =>
          val pt = curve(turn * count.toDouble)
          marker(pt).at(pt).on(loop(n - 1))
      }
    }

    loop(points)
  }

  val marker = (point: Point) =>
    circle(point.r * 0.125 + 7)
      .fillColor(Color.red.spin(point.angle / 4.0))
      .noStroke

  val picture =
    drawCurve(20, marker, parametricSpiral _)
}
