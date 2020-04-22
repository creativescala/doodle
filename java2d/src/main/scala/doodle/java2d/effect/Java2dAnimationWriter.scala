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
package java2d
package effect

import cats.Monoid
import cats.effect.IO
import doodle.effect.Writer.Gif
import doodle.interact.effect.AnimationWriter
import java.io.{File, FileOutputStream}
// import javax.imageio.{IIOImage,ImageIO,ImageWriter,ImageTypeSpecifier}
// import javax.imageio.metadata.IIOMetadataNode
// import javax.imageio.stream.FileImageOutputStream
import java.awt.image.BufferedImage
import monix.eval.{Task, TaskLift}
import monix.execution.Scheduler
import monix.reactive.{Consumer, Observable}

/**
 * Write an animation as an animated GIF. The GIF file format doesn't support
 * transparency to the degree we need to faithfully render Java2d images. In
 * particular it doesn't support semi-transparent redraw. As a result we just
 * fill with the background color on each frame, if the background is set.
 */
object Java2dAnimationWriter
    extends AnimationWriter[doodle.java2d.Algebra, Drawing, Frame, Gif] {

  val gifEncoder: IO[GifEncoder] =
    IO { new GifEncoder() }

  def write[A](file: File, frame: Frame, frames: Observable[Picture[A]])(
      implicit s: Scheduler,
      m: Monoid[A]
  ): IO[A] = {
    for {
      ge <- gifEncoder
      _ = ge.start(new FileOutputStream(file))
      _ = ge.setDelay(13)
      a <- frames
        .consumeWith(Consumer.foldLeft(IO(m.empty)) { (accum, picture) =>
          for {
            a <- accum
            result <- doodle.java2d.effect.Java2dWriter
              .renderBufferedImage(frame.size, frame.center, frame.background, picture)(
                (w, h) => new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
              )
            (bi, a2) = result
            _ = ge.addFrame(bi)
          } yield m.combine(a, a2)
        })
        .to[IO](TaskLift.toIO(Task.catsEffect(s)))
        .flatMap(ioa => ioa)

      _ = ge.finish()
    } yield a
  }
}
