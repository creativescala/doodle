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
package java2d
package effect

import cats.Monoid
import cats.effect.IO
import doodle.core.format.Gif
import doodle.interact.effect.AnimationWriter
import fs2.Stream

import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream

/** Write an animation as an animated GIF. The GIF file format doesn't support
  * transparency to the degree we need to faithfully render Java2d images. In
  * particular it doesn't support semi-transparent redraw. As a result we just
  * fill with the background color on each frame, if the background is set.
  */
object Java2dAnimationWriter
    extends AnimationWriter[doodle.java2d.Algebra, Frame, Gif] {

  val gifEncoder: IO[GifEncoder] =
    IO { new GifEncoder() }

  def write[A](file: File, frame: Frame, frames: Stream[IO, Picture[A]])(
      implicit m: Monoid[A]
  ): IO[A] = {
    for {
      ge <- gifEncoder
      _ = ge.start(new FileOutputStream(file))
      _ = ge.setDelay(20)
      a <- frames
        .evalMap { picture =>
          for {
            result <- doodle.java2d.effect.Java2dWriter
              .renderBufferedImage(
                frame.size,
                frame.center,
                frame.background,
                picture
              )((w, h) => new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB))
            (bi, a2) = result
            _ = ge.addFrame(bi)
          } yield a2
        }
        .compile
        .foldMonoid
      _ = ge.finish()
    } yield a
  }
}
