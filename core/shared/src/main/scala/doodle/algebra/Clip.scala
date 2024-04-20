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

import doodle.core.ClosedPath

trait Clip extends Algebra {

  def clip[A](img: Drawing[A], clipPath: ClosedPath): Drawing[A]
}

trait ClipConstructor {
  self: BaseConstructor { type Algebra <: Clip } =>

  def clip(image: Picture[Unit], clipPath: ClosedPath): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.Drawing[Unit] = 
        algebra.clip(image(algebra), clipPath)
    }
}
