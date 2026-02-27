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
package algebra
package generic

trait GenericBlend[G[_]] extends doodle.algebra.Blend {
  self: doodle.algebra.Algebra { type Drawing[A] = Finalized[G, A] } =>

  trait BlendApi {
    def applyBlend[A](
        image: Finalized[G, A],
        blendMode: BlendMode
    ): Finalized[G, A]
  }

  def BlendApi: BlendApi

  def normal[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.Normal)

  def darken[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.Darken)

  def multiply[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.Multiply)

  def colorBurn[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.ColorBurn)

  def lighten[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.Lighten)

  def screen[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.Screen)

  def colorDodge[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.ColorDodge)

  def overlay[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.Overlay)

  def softLight[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.SoftLight)

  def hardLight[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.HardLight)

  def difference[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.Difference)

  def exclusion[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.Exclusion)

  def hue[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.Hue)

  def saturation[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.Saturation)

  def color[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.Color)

  def luminosity[A](image: Drawing[A]): Drawing[A] =
    BlendApi.applyBlend(image, BlendMode.Luminosity)
}
