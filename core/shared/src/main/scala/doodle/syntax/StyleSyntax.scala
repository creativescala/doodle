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
import doodle.algebra.Style
import doodle.core.Color
import doodle.core.Gradient

trait StyleSyntax {
  implicit class StylePictureOps[Alg[x[_]] <: Style[x], F[_], A](
      picture: Picture[Alg, F, A]
  ) {
    def fillColor(fillColor: Color): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.fillColor(picture(algebra), fillColor)
      }

    def fillGradient(fillGradient: Gradient): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.fillGradient(picture(algebra), fillGradient)
      }

    def strokeColor(strokeColor: Color): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.strokeColor(picture(algebra), strokeColor)
      }

    def strokeWidth(strokeWidth: Double): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.strokeWidth(picture(algebra), strokeWidth)
      }

    def strokeDash(pattern: Array[Double]): Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.strokeDash(picture(algebra), pattern)
      }

    def noDash: Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.noDash(picture(algebra))
      }

    def noFill: Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.noFill(picture(algebra))
      }

    def noStroke: Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        algebra.noStroke(picture(algebra))
      }
  }
}
