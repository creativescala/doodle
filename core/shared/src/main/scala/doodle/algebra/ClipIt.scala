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

import doodle.core.font.Font

/** Algebra for creating and styling text.
  */
trait ClipIt extends Algebra {

  /** Specifies the font to use when rendering text
    */
  def cfont[A](image: Drawing[A], font: Font): Drawing[A]

  /** Render the given String
    */
  def clipit(text: String): Drawing[Unit]
}

/** Constructors for Text algebra
  */
trait ClipItConstructor {
  self: BaseConstructor { type Algebra <: ClipIt } =>

  /** Render the given String
    */
  def clipit(text: String): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
        algebra.clipit(text)
    }
}
