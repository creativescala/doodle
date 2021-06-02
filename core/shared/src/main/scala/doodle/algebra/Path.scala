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

import doodle.core.{Angle, ClosedPath, PathElement, Point, OpenPath}

trait Path[F[_]] extends Algebra[F] {
  def path(path: ClosedPath): F[Unit]
  def path(path: OpenPath): F[Unit]

  // Derived methods ------------

  def regularPolygon(sides: Int, radius: Double, angle: Angle): F[Unit] = {
    path(ClosedPath(PathElement.regularPolygon(sides, radius, angle)))
  }

  def star(points: Int,
           outerRadius: Double,
           innerRadius: Double,
           angle: Angle): F[Unit] = {
    path(ClosedPath(PathElement.star(points, outerRadius, innerRadius, angle)))
  }

  def roundedRectangle(width: Double,
                       height: Double,
                       radius: Double): F[Unit] = {
    path(ClosedPath(PathElement.roundedRectangle(width, height, radius)))
  }

  def equilateralTriangle(width: Double): F[Unit] = {
    path(ClosedPath(PathElement.equilateralTriangle(width)))
  }

  def interpolatingSpline(points: Seq[Point]): F[Unit] =
    path(OpenPath(PathElement.interpolatingSpline(points)))

  def catmulRom(points: Seq[Point], tension: Double = 0.5): F[Unit] =
    path(OpenPath(PathElement.catmulRom(points, tension)))
}
