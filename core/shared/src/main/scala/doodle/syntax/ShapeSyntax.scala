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

import doodle.algebra.Picture
import doodle.algebra.Shape

trait ShapeSyntax {
  def rectangle[Alg[x[_]] <: Shape[x], F[_]](
      width: Double,
      height: Double
  ): Picture[Alg, F, Unit] =
    Picture { implicit algebra: Alg[F] =>
      algebra.rectangle(width, height)
    }

  def square[Alg[x[_]] <: Shape[x], F[_]](
      width: Double
  ): Picture[Alg, F, Unit] =
    Picture { implicit algebra: Alg[F] =>
      algebra.square(width)
    }

  def triangle[Alg[x[_]] <: Shape[x], F[_]](
      width: Double,
      height: Double
  ): Picture[Alg, F, Unit] =
    Picture { implicit algebra: Alg[F] =>
      algebra.triangle(width, height)
    }

  def circle[Alg[x[_]] <: Shape[x], F[_]](
      diameter: Double
  ): Picture[Alg, F, Unit] =
    Picture { implicit algebra: Alg[F] =>
      algebra.circle(diameter)
    }

  def empty[Alg[x[_]] <: Shape[x], F[_]]: Picture[Alg, F, Unit] =
    Picture { implicit algebra: Alg[F] =>
      algebra.empty
    }
}
