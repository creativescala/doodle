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

import doodle.core.Base64
import doodle.core.format._

/** Algebra indicating a Picture can be created from a base 64 encoded bitmap in
  * Gif format.
  */
trait FromGifBase64 extends Algebra {

  /** Create a picture from a Base64 encoded bitmap in the given format. The
    * format is reified as a value so the method can dispatch on it.
    */
  def fromGifBase64(base64: Base64[Gif]): F[Unit]
}

/** Algebra indicating a Picture can be created from a base 64 encoded bitmap in
  * PNG format.
  */
trait FromPngBase64 extends Algebra {

  /** Create a picture from a Base64 encoded bitmap in the given format. The
    * format is reified as a value so the method can dispatch on it.
    */
  def fromPngBase64(base64: Base64[Png]): F[Unit]
}

/** Algebra indicating a Picture can be created from a base 64 encoded bitmap in
  * JPG format.
  */
trait FromJpgBase64 extends Algebra {

  /** Create a picture from a Base64 encoded bitmap in the given format. The
    * format is reified as a value so the method can dispatch on it.
    */
  def fromJpgBase64(base64: Base64[Jpg]): F[Unit]
}

/** Constructor for FromGifBase64 algebra */
trait FromGifBase64Constructor {
  self: BaseConstructor { type Algebra <: FromGifBase64 } =>

  def fromBase64(base64: Base64[Gif]): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.F[Unit] =
        algebra.fromGifBase64(base64)
    }
}

/** Constructor for FromPngBase64 algebra */
trait FromPngBase64Constructor {
  self: BaseConstructor { type Algebra <: FromPngBase64 } =>

  def fromBase64(base64: Base64[Png]): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.F[Unit] =
        algebra.fromPngBase64(base64)
    }
}

/** Constructor for FromJpgBase64 algebra */
trait FromJpgBase64Constructor {
  self: BaseConstructor { type Algebra <: FromJpgBase64 } =>

  def fromBase64(base64: Base64[Jpg]): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.F[Unit] =
        algebra.fromJpgBase64(base64)
    }
}
