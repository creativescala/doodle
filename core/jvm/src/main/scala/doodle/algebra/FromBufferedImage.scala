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

package doodle.algebra

import java.awt.image.BufferedImage

/** Algebra for converting a BufferedImage to a picture */
trait FromBufferedImage extends Algebra {
  def fromBufferedImage(in: BufferedImage): Drawing[Unit]
}

/** Constructor for FromBufferedImage algebra */
trait FromBufferedImageConstructor {
  self: BaseConstructor { type Algebra <: FromBufferedImage } =>

  def fromBase64(image: BufferedImage): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
        algebra.fromBufferedImage(image)
    }
}
