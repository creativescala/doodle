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
package algebra
package generic

import doodle.core.Color

trait GenericStyle[G,A] extends Style[Finalized[G,?],A] {
  def fillColor(image: Finalized[G,A], fillColor: Color): Finalized[G,A] =
    Finalized.contextTransform(_.fillColor(fillColor))(image)

  def strokeColor(image: Finalized[G,A], strokeColor: Color): Finalized[G,A] =
    Finalized.contextTransform(_.strokeColor(strokeColor))(image)

  def strokeWidth(image: Finalized[G,A], strokeWidth: Double): Finalized[G,A] =
    Finalized.contextTransform(_.strokeWidth(strokeWidth))(image)

  def noFill(image: Finalized[G,A]): Finalized[G,A] =
    Finalized.contextTransform(_.noFill)(image)

  def noStroke(image: Finalized[G,A]): Finalized[G,A] =
    Finalized.contextTransform(_.noStroke)(image)
}
