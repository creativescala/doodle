/*
 * Copyright 2015 Noel Welsh
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

import doodle.core.Cap
import doodle.core.Color
import doodle.core.Gradient
import doodle.core.Join

trait GenericStyle[G[_]] extends Style {
  self: Algebra { type Drawing[A] = Finalized[G, A] } =>

  def fillColor[A](image: Finalized[G, A], fillColor: Color): Finalized[G, A] =
    Finalized.contextTransform(_.fillColor(fillColor))(image)

  def fillGradient[A](
      image: Finalized[G, A],
      fillGradient: Gradient
  ): Finalized[G, A] =
    Finalized.contextTransform(_.fillGradient(fillGradient))(image)

  def strokeColor[A](
      image: Finalized[G, A],
      strokeColor: Color
  ): Finalized[G, A] =
    Finalized.contextTransform(_.strokeColor(strokeColor))(image)

  def strokeWidth[A](
      image: Finalized[G, A],
      strokeWidth: Double
  ): Finalized[G, A] =
    Finalized.contextTransform(_.strokeWidth(strokeWidth))(image)

  def strokeCap[A](image: Finalized[G, A], cap: Cap): Finalized[G, A] =
    Finalized.contextTransform(_.strokeCap(cap))(image)

  def strokeJoin[A](image: Finalized[G, A], join: Join): Finalized[G, A] =
    Finalized.contextTransform(_.strokeJoin(join))(image)

  def strokeDash[A](
      image: Finalized[G, A],
      pattern: Iterable[Double]
  ): Finalized[G, A] =
    Finalized
      .contextTransform(_.strokeDash(pattern.toArray.map(_.toFloat)))(image)

  def noDash[A](image: Finalized[G, A]): Finalized[G, A] =
    Finalized.contextTransform(_.noDash)(image)

  def noFill[A](image: Finalized[G, A]): Finalized[G, A] =
    Finalized.contextTransform(_.noFill)(image)

  def noStroke[A](image: Finalized[G, A]): Finalized[G, A] =
    Finalized.contextTransform(_.noStroke)(image)
}
