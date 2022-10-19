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

import doodle.core.Color

trait Debug extends Algebra {

  /** Draws the bounding box and origin of the given picture on top of the
    * picture. The given color is used for the bounding box and origin.
    *
    * The bounding box and origin are not included in layout calculations and
    * hence will not affect the picture's layout.
    */
  def debug[A](picture: F[A], color: Color): F[A]
}
