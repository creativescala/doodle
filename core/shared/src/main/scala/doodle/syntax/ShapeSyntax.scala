/*
 * Copyright 2015 Noel Welsh
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

import doodle.algebra.Picture
import doodle.algebra.Shape

trait ShapeSyntax {
  def rectangle[Alg <: Shape](
      width: Double,
      height: Double
  ): Picture[Alg, Unit] =
    new Picture[Alg, Unit] {
      def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
        algebra.rectangle(width, height)
    }

  def square[Alg <: Shape](
      width: Double
  ): Picture[Alg, Unit] =
    new Picture[Alg, Unit] {
      def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
        algebra.square(width)
    }

  def triangle[Alg <: Shape](
      width: Double,
      height: Double
  ): Picture[Alg, Unit] =
    new Picture[Alg, Unit] {
      def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
        algebra.triangle(width, height)
    }

  def circle[Alg <: Shape](
      diameter: Double
  ): Picture[Alg, Unit] =
    new Picture[Alg, Unit] {
      def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
        algebra.circle(diameter)
    }

  def empty[Alg <: Shape]: Picture[Alg, Unit] =
    new Picture[Alg, Unit] {
      def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
        algebra.empty
    }
}
