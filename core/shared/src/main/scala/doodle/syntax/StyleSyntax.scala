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
package syntax

import doodle.algebra.{Picture, Style}
import doodle.core.Color

trait StyleSyntax {
  implicit class StyleOps[F[_], A](picture: F[A]) {
    def fillColor(fillColor: Color)(implicit s: Style[F]): F[A] =
      s.fillColor(picture, fillColor)

    def strokeColor(strokeColor: Color)(implicit s: Style[F]): F[A] =
      s.strokeColor(picture, strokeColor)

    def strokeWidth(strokeWidth: Double)(implicit s: Style[F]): F[A] =
      s.strokeWidth(picture, strokeWidth)

    def noFill(implicit s: Style[F]): F[A] =
      s.noFill(picture)

    def noStroke(implicit s: Style[F]): F[A] =
      s.noStroke(picture)
  }

  implicit class StylePictureOps[Alg[x[_]] <: Style[x], F[_], A](
      picture: Picture[Alg, F, A]) {
    def fillColor(fillColor: Color): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).fillColor(fillColor)
      }

    def strokeColor(strokeColor: Color): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).strokeColor(strokeColor)
      }

    def strokeWidth(strokeWidth: Double): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).strokeWidth(strokeWidth)
      }

    def noFill: Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).noFill
      }

    def noStroke: Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).noStroke
      }
  }
}
