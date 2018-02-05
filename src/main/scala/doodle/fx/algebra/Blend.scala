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
package fx
package algebra

import javafx.scene.effect.BlendMode

trait Blend[A] extends doodle.algebra.Blend[Drawing, A] {
  def screen(image: Drawing[A]): Drawing[A] =
    Drawing.contextTransform(dc => dc.blendMode(BlendMode.SCREEN))(image)
  def burn(image: Drawing[A]): Drawing[A] =
    Drawing.contextTransform(dc => dc.blendMode(BlendMode.COLOR_BURN))(image)
  def dodge(image: Drawing[A]): Drawing[A] =
    Drawing.contextTransform(dc => dc.blendMode(BlendMode.COLOR_DODGE))(image)
  def lighten(image: Drawing[A]): Drawing[A] =
    Drawing.contextTransform(dc => dc.blendMode(BlendMode.LIGHTEN))(image)
  def sourceOver(image: Drawing[A]): Drawing[A] =
    Drawing.contextTransform(dc => dc.blendMode(BlendMode.SRC_OVER))(image)
}
