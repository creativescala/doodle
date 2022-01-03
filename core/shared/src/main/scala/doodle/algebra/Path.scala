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
package algebra

import doodle.core.Angle
import doodle.core.ClosedPath
import doodle.core.OpenPath
import doodle.core.PathElement
import doodle.core.Point

trait Path[F[_]] extends Algebra[F] {
  def path(path: ClosedPath): F[Unit]
  def path(path: OpenPath): F[Unit]

  // Derived methods ------------

  def regularPolygon(sides: Int, radius: Double): F[Unit] = {
    path(ClosedPath(PathElement.regularPolygon(sides, radius)))
  }

  def star(points: Int, outerRadius: Double, innerRadius: Double): F[Unit] = {
    path(ClosedPath(PathElement.star(points, outerRadius, innerRadius)))
  }

  def roundedRectangle(
      width: Double,
      height: Double,
      radius: Double
  ): F[Unit] = {
    path(ClosedPath(PathElement.roundedRectangle(width, height, radius)))
  }

  /** Create an equilateral triangle with the given side length. */
  def equilateralTriangle(width: Double): F[Unit] = {
    path(ClosedPath(PathElement.equilateralTriangle(width)))
  }

  def interpolatingSpline(points: Seq[Point]): F[Unit] =
    path(OpenPath(PathElement.interpolatingSpline(points)))

  def catmulRom(points: Seq[Point], tension: Double = 0.5): F[Unit] =
    path(OpenPath(PathElement.catmulRom(points, tension)))
}
