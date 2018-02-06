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

import doodle.algebra.{Image,Blend}

trait BlendSyntax {
  implicit class BlendOps[F[_],A](image: F[A]) {
    def screen(implicit b: Blend[F,A]): F[A] =
      b.screen(image)

    def burn(implicit b: Blend[F,A]): F[A] =
      b.burn(image)

    def dodge(implicit b: Blend[F,A]): F[A] =
      b.dodge(image)

    def lighten(implicit b: Blend[F,A]): F[A] =
      b.lighten(image)

    def sourceOver(implicit b: Blend[F,A]): F[A] =
      b.sourceOver(image)
  }

  implicit class BlendImageOps[Algebra <: Blend[F,A],F[_],A](image: Image[Algebra,F,A]) {
    def screen: Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).screen
      }

    def burn: Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).burn
      }

    def dodge: Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).dodge
      }

    def lighten: Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).lighten
      }

    def sourceOver: Image[Algebra,F,A] =
      Image{ implicit algebra: Algebra =>
        image(algebra).sourceOver
      }
  }
}
