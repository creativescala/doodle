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

package doodle.java2d

import doodle.algebra.ToPicture
import doodle.core.Base64
import doodle.core.format._
import doodle.core.ClosedPath

import java.awt.image.BufferedImage
import javax.sound.sampled.Clip

/** ToPicture instances for the Java2d backend */
trait Java2dToPicture {
  implicit val bufferedImageToPicture: ToPicture[BufferedImage, Algebra] =
    new ToPicture[BufferedImage, Algebra] {
      def toPicture(in: BufferedImage): Picture[Unit] =
        new Picture[Unit] {
          def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
            algebra.fromBufferedImage(in)
        }
    }

  implicit val base64GifToPicture: ToPicture[Base64[Gif], Algebra] =
    new ToPicture[Base64[Gif], Algebra] {
      def toPicture(in: Base64[Gif]): Picture[Unit] =
        new Picture[Unit] {
          def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
            algebra.fromGifBase64(in)
        }
    }

  implicit val base64PngToPicture: ToPicture[Base64[Png], Algebra] =
    new ToPicture[Base64[Png], Algebra] {
      def toPicture(in: Base64[Png]): Picture[Unit] = {
        new Picture[Unit] {
          def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
            algebra.fromPngBase64(in)
        }
      }
    }

  implicit val base64JpgToPicture: ToPicture[Base64[Jpg], Algebra] =
    new ToPicture[Base64[Jpg], Algebra] {
      def toPicture(in: Base64[Jpg]): Picture[Unit] =
        new Picture[Unit] {
          def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
            algebra.fromJpgBase64(in)
        }
    }

}
