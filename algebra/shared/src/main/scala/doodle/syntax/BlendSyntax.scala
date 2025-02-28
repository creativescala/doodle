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

import doodle.algebra.Algebra
import doodle.algebra.Blend
import doodle.algebra.Picture

trait BlendSyntax {
  implicit class BlendPictureOps[Alg <: Algebra, A](
      picture: Picture[Alg, A]
  ) {
    def screen: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.screen(picture(algebra))
      }

    def burn: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.burn(picture(algebra))
      }

    def dodge: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.dodge(picture(algebra))
      }

    def lighten: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.lighten(picture(algebra))
      }

    def sourceOver: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.sourceOver(picture(algebra))
      }
  }
}
