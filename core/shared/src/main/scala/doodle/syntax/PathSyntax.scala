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
package syntax

import doodle.algebra.Path
import doodle.algebra.Picture
import doodle.core.ClosedPath
import doodle.core.OpenPath
import doodle.core.Point

trait PathSyntax {
  implicit class ClosedPathOps(closedPath: ClosedPath) {
    def path[Alg <: Path]: Picture[Alg, Unit] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[Unit] =
          algebra.path(closedPath)
      }
  }

  implicit class OpenPathOps(openPath: OpenPath) {
    def path[Alg <: Path]: Picture[Alg, Unit] =
      new Picture {
        def apply(implicit algebra: Alg): algebra.F[Unit] =
          algebra.path(openPath)
      }
  }

  def regularPolygon[Alg <: Path](
      sides: Int,
      radius: Double
  ): Picture[Alg, Unit] =
    new Picture {
      def apply(implicit algebra: Alg): algebra.F[Unit] =
        algebra.regularPolygon(sides, radius)
    }

  def star[Alg <: Path](
      points: Int,
      outerRadius: Double,
      innerRadius: Double
  ): Picture[Alg, Unit] =
    new Picture {
      def apply(implicit algebra: Alg): algebra.F[Unit] =
        algebra.star(points, outerRadius, innerRadius)
    }

  def roundedRectangle[Alg <: Path](
      width: Double,
      height: Double,
      radius: Double
  ): Picture[Alg, Unit] =
    new Picture {
      def apply(implicit algebra: Alg): algebra.F[Unit] =
        algebra.roundedRectangle(width, height, radius)
    }

  def equilateralTriangle[Alg <: Path](
      width: Double
  ): Picture[Alg, Unit] =
    new Picture {
      def apply(implicit algebra: Alg): algebra.F[Unit] =
        algebra.equilateralTriangle(width)
    }

  def interpolatingSpline[Alg <: Path](
      points: Seq[Point]
  ): Picture[Alg, Unit] =
    new Picture {
      def apply(implicit algebra: Alg): algebra.F[Unit] =
        algebra.interpolatingSpline(points)
    }

  def catmulRom[Alg <: Path](
      points: Seq[Point],
      tension: Double = 0.5
  ): Picture[Alg, Unit] =
    new Picture {
      def apply(implicit algebra: Alg): algebra.F[Unit] =
        algebra.catmulRom(points, tension)
    }
}
