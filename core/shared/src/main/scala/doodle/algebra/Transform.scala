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

import doodle.core.Angle
import doodle.core.Transform as Tx
import doodle.core.Vec

trait Transform extends Algebra {
  def transform[A](img: Drawing[A], tx: Tx): Drawing[A]

  // Derived methods

  def scale[A](img: Drawing[A], x: Double, y: Double): Drawing[A] =
    transform(img, Tx.scale(x, y))
  def rotate[A](img: Drawing[A], angle: Angle): Drawing[A] =
    transform(img, Tx.rotate(angle))
  def translate[A](img: Drawing[A], x: Double, y: Double): Drawing[A] =
    transform(img, Tx.translate(x, y))
  def translate[A](img: Drawing[A], vec: Vec): Drawing[A] =
    transform(img, Tx.translate(vec))
  def horizontalReflection[A](img: Drawing[A]): Drawing[A] =
    transform(img, Tx.horizontalReflection)
  def verticalReflection[A](img: Drawing[A]): Drawing[A] =
    transform(img, Tx.verticalReflection)
}
