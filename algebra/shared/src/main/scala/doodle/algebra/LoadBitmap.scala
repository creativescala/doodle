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

import cats.effect.IO

/** Algebra for loading bitmap images from various sources.
  *
  * This algebra is parameterized by:
  * @tparam Specifier
  *   the type used to specify where to find a bitmap (e.g. File on JVM, String
  *   URL on JS)
  * @tparam Bitmap
  *   the type of the resulting bitmap (e.g. BufferedImage on JVM,
  *   HTMLImageElement on JS)
  */
trait LoadBitmap[Specifier, Bitmap] {

  /** Load a bitmap from the given specifier.
    *
    * Returns an IO that either succeeds with the loaded bitmap or fails with a
    * BitmapError.
    */
  def load(specifier: Specifier): IO[Bitmap]
}

object LoadBitmap {

  /** Summon an instance of LoadBitmap */
  def apply[S, B](using lb: LoadBitmap[S, B]): LoadBitmap[S, B] = lb
}
