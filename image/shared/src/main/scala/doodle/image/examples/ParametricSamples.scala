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

import cats.instances.list._
import doodle.core._
import doodle.image.Image
import doodle.image.syntax.all._
import doodle.syntax.all._

object ParametricSamples {
  val color = Color.lightSlateGray.alpha(0.3.normalized)
  val dot = Image.circle(2).fillColor(color).noStroke

  def render[A](curve: Parametric[A], count: Int): Image =
    ((curve.sample(count)) map { pt =>
      dot.at(pt.toVec)
    }).allOn

  def circle(count: Int) =
    render(Parametric.circle(200), count)

  def rose(count: Int) =
    render(Parametric.rose(3, 200), count)

  def logarithmicSpiral(count: Int) =
    render(
      Parametric.logarithmicSpiral(1, 0.25).toNormalizedCurve(1440.degrees),
      count
    )

  def bezier(count: Int) =
    render(
      Parametric.quadraticBezier(Point.zero, Point(100, 200), Point(200, 0)),
      count
    )

  def interpolate[A](count: Int, f: Parametric[A]) =
    Parametric.interpolate(f.sample(count))

  def interpolatedCircle(count: Int, interps: Int) =
    render(interpolate(interps, Parametric.circle(200)), count)

}
