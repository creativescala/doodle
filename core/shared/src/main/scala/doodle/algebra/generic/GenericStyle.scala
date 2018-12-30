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

import doodle.core.{Cap, Color, Join}

trait GenericStyle extends Style[Finalized[?]] {
  def fillColor[A](image: Finalized[A], fillColor: Color): Finalized[A] =
    Finalized.contextTransform(_.fillColor(fillColor))(image)

  def strokeColor[A](image: Finalized[A], strokeColor: Color): Finalized[A] =
    Finalized.contextTransform(_.strokeColor(strokeColor))(image)

  def strokeWidth[A](image: Finalized[A], strokeWidth: Double): Finalized[A] =
    Finalized.contextTransform(_.strokeWidth(strokeWidth))(image)

  def strokeCap[A](image: Finalized[A], cap: Cap): Finalized[A] =
    Finalized.contextTransform(_.strokeCap(cap))(image)

  def strokeJoin[A](image: Finalized[A], join: Join): Finalized[A] =
    Finalized.contextTransform(_.strokeJoin(join))(image)

  def noFill[A](image: Finalized[A]): Finalized[A] =
    Finalized.contextTransform(_.noFill)(image)

  def noStroke[A](image: Finalized[A]): Finalized[A] =
    Finalized.contextTransform(_.noStroke)(image)
}
