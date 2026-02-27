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

/** Algebra describing color blending modes. */
trait Blend extends Algebra {
  def normal[A](image: Drawing[A]): Drawing[A]
  def darken[A](image: Drawing[A]): Drawing[A]
  def multiply[A](image: Drawing[A]): Drawing[A]
  def colorBurn[A](image: Drawing[A]): Drawing[A]
  def lighten[A](image: Drawing[A]): Drawing[A]
  def screen[A](image: Drawing[A]): Drawing[A]
  def colorDodge[A](image: Drawing[A]): Drawing[A]
  def overlay[A](image: Drawing[A]): Drawing[A]
  def softLight[A](image: Drawing[A]): Drawing[A]
  def hardLight[A](image: Drawing[A]): Drawing[A]
  def difference[A](image: Drawing[A]): Drawing[A]
  def exclusion[A](image: Drawing[A]): Drawing[A]
  def hue[A](image: Drawing[A]): Drawing[A]
  def saturation[A](image: Drawing[A]): Drawing[A]
  def color[A](image: Drawing[A]): Drawing[A]
  def luminosity[A](image: Drawing[A]): Drawing[A]
}
