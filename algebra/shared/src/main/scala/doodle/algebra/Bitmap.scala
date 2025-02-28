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

import java.io.File

trait Bitmap extends Algebra {

  /** Read an image from the given file
    */
  def read(file: File): Drawing[Unit]

  /** Convenience to read an image from the file specified in the given String
    */
  def read(file: String): Drawing[Unit] =
    read(new File(file))
}

/** Constructors for Bitmap algebra
  */
trait BitmapConstructor {
  self: BaseConstructor { type Algebra <: Bitmap } =>

  /** Read an image from the given file
    */
  def read(file: File): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
        algebra.read(file)
    }

  /** Convenience to read an image from the file specified in the given String
    */
  def read(file: String): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
        algebra.read(file)
    }
}
