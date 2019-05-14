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

import doodle.algebra.{Image, Blend}

trait BlendSyntax {
  implicit class BlendOps[F[_], A](image: F[A]) {
    def screen(implicit b: Blend[F]): F[A] =
      b.screen(image)

    def burn(implicit b: Blend[F]): F[A] =
      b.burn(image)

    def dodge(implicit b: Blend[F]): F[A] =
      b.dodge(image)

    def lighten(implicit b: Blend[F]): F[A] =
      b.lighten(image)

    def sourceOver(implicit b: Blend[F]): F[A] =
      b.sourceOver(image)
  }

  implicit class BlendImageOps[Alg[x[_]] <: Blend[x], F[_], A](
      image: Image[Alg, F, A]) {
    def screen: Image[Alg, F, A] =
      Image { implicit algebra: Alg[F] =>
        image(algebra).screen
      }

    def burn: Image[Alg, F, A] =
      Image { implicit algebra: Alg[F] =>
        image(algebra).burn
      }

    def dodge: Image[Alg, F, A] =
      Image { implicit algebra: Alg[F] =>
        image(algebra).dodge
      }

    def lighten: Image[Alg, F, A] =
      Image { implicit algebra: Alg[F] =>
        image(algebra).lighten
      }

    def sourceOver: Image[Alg, F, A] =
      Image { implicit algebra: Alg[F] =>
        image(algebra).sourceOver
      }
  }
}
