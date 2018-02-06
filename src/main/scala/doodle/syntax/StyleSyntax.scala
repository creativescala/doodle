/*
 * Copyright 2015 noelwelsh
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

import doodle.algebra.{Image,Style}

trait StyleSyntax {
  implicit class StyleOps[F[_],C,A](image: F[A]) {
    def fillColor(fillColor: C)(implicit s: Style[F,C,A]): F[A] =
      s.fillColor(image, fillColor)

    def strokeColor(strokeColor: C)(implicit s: Style[F,C,A]): F[A] =
      s.strokeColor(image, strokeColor)

    def noFill(implicit s: Style[F,C,A]): F[A] =
      s.noFill(image)

    def noStroke(implicit s: Style[F,C,A]): F[A] =
      s.noStroke(image)
  }

  implicit class StyleImageOps[Algebra <: Style[F,C,A],F[_],C,A](image: Image[Algebra,F,A]) {
    def fillColor(fillColor: C): Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).fillColor(fillColor)
      }

    def strokeColor(strokeColor: C): Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).strokeColor(strokeColor)
      }

    def noFill: Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).noFill
      }

    def noStroke: Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).noStroke
      }
  }
}
