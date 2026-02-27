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
    def normal: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.normal(picture(algebra))
      }

    def darken: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.darken(picture(algebra))
      }

    def multiply: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.multiply(picture(algebra))
      }

    def colorBurn: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.colorBurn(picture(algebra))
      }

    def lighten: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.lighten(picture(algebra))
      }

    def screen: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.screen(picture(algebra))
      }

    def colorDodge: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.colorDodge(picture(algebra))
      }

    def overlay: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.overlay(picture(algebra))
      }

    def softLight: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.softLight(picture(algebra))
      }

    def hardLight: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.hardLight(picture(algebra))
      }

    def difference: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.difference(picture(algebra))
      }

    def exclusion: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.exclusion(picture(algebra))
      }

    def hue: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.hue(picture(algebra))
      }

    def saturation: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.saturation(picture(algebra))
      }

    def color: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.color(picture(algebra))
      }

    def luminosity: Picture[Alg with Blend, A] =
      new Picture[Alg with Blend, A] {
        def apply(implicit algebra: Alg with Blend): algebra.Drawing[A] =
          algebra.luminosity(picture(algebra))
      }
  }
}
