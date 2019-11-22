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
 * WITHOUT WARRANTIES OR CONDITIONS OReification ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doodle
package java2d
package algebra

import cats.Semigroup
import doodle.language.Basic
import doodle.algebra._
import doodle.algebra.generic._
import doodle.java2d.algebra.reified._
import java.awt.Graphics2D

final case class Algebra(gc: Graphics2D)
    extends Layout[Finalized[Reification, ?]]
    with Size[Finalized[Reification, ?]]
    with ReifiedPath
    with ReifiedShape
    with ReifiedText
    with GenericStyle[Reification]
    with GenericTransform[Reification]
    with ReifiedBitmap
    with Basic[Drawing] {

  // Layout ----------------------------------------------------------

  val layout = ReifiedLayout.instance

  def on[A](top: Finalized[Reification, A], bottom: Finalized[Reification, A])(
      implicit s: Semigroup[A]): Finalized[Reification, A] =
    layout.on(top, bottom)(s)

  def beside[A](left: Finalized[Reification, A],
                right: Finalized[Reification, A])(
      implicit s: Semigroup[A]): Finalized[Reification, A] =
    layout.beside(left, right)(s)

  def above[A](top: Finalized[Reification, A],
               bottom: Finalized[Reification, A])(
      implicit s: Semigroup[A]): Finalized[Reification, A] =
    layout.above(top, bottom)(s)

  def at[A](img: Finalized[Reification, A],
            x: Double,
            y: Double): Finalized[Reification, A] =
    layout.at(img, x, y)

  // Size ------------------------------------------------------------

  val size = ReifiedSize.instance

  def width[A](image: Finalized[Reification, A]): Finalized[Reification, Double] =
    size.width(image)

  def height[A](image: Finalized[Reification, A]): Finalized[Reification, Double] =
    size.height(image)

  def size[A](image: Finalized[Reification, A]): Finalized[Reification, (Double, Double)] =
    size.size(image)
}
