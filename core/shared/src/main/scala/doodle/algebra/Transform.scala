/*
 * Copyright 2015-2020 Noel Welsh
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
import doodle.core.Vec
import doodle.core.{Transform => Tx}

trait Transform extends Algebra {
  def transform[A](img: F[A], tx: Tx): F[A]

  // Derived methods

  def scale[A](img: F[A], x: Double, y: Double): F[A] =
    transform(img, Tx.scale(x, y))
  def rotate[A](img: F[A], angle: Angle): F[A] =
    transform(img, Tx.rotate(angle))
  def translate[A](img: F[A], x: Double, y: Double): F[A] =
    transform(img, Tx.translate(x, y))
  def translate[A](img: F[A], vec: Vec): F[A] =
    transform(img, Tx.translate(vec))
  def horizontalReflection[A](img: F[A]): F[A] =
    transform(img, Tx.horizontalReflection)
  def verticalReflection[A](img: F[A]): F[A] =
    transform(img, Tx.verticalReflection)
}
