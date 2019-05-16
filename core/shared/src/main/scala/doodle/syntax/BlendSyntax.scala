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

import doodle.algebra.{Picture, Blend}

trait BlendSyntax {
  implicit class BlendOps[F[_], A](picture: F[A]) {
    def screen(implicit b: Blend[F]): F[A] =
      b.screen(picture)

    def burn(implicit b: Blend[F]): F[A] =
      b.burn(picture)

    def dodge(implicit b: Blend[F]): F[A] =
      b.dodge(picture)

    def lighten(implicit b: Blend[F]): F[A] =
      b.lighten(picture)

    def sourceOver(implicit b: Blend[F]): F[A] =
      b.sourceOver(picture)
  }

  implicit class BlendPictureOps[Alg[x[_]] <: Blend[x], F[_], A](
      picture: Picture[Alg, F, A]) {
    def screen: Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).screen
      }

    def burn: Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).burn
      }

    def dodge: Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).dodge
      }

    def lighten: Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).lighten
      }

    def sourceOver: Picture[Alg, F, A] =
      Picture { implicit algebra: Alg[F] =>
        picture(algebra).sourceOver
      }
  }
}
