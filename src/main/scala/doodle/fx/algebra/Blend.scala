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

import doodle.algebra.generic.{BlendMode, Finalized}

trait Blend extends doodle.algebra.Blend[Drawing] {
  def screen[A](image: Drawing[A]): Drawing[A] =
    Finalized.contextTransform(dc => dc.blendMode(BlendMode.screen))(image)
  def burn[A](image: Drawing[A]): Drawing[A] =
    Finalized.contextTransform(dc => dc.blendMode(BlendMode.burn))(image)
  def dodge[A](image: Drawing[A]): Drawing[A] =
    Finalized.contextTransform(dc => dc.blendMode(BlendMode.dodge))(image)
  def lighten[A](image: Drawing[A]): Drawing[A] =
    Finalized.contextTransform(dc => dc.blendMode(BlendMode.lighten))(image)
  def sourceOver[A](image: Drawing[A]): Drawing[A] =
    Finalized.contextTransform(dc => dc.blendMode(BlendMode.sourceOver))(image)
}
