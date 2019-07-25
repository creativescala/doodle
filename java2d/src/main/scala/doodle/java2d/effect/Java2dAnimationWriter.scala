/*
 * Copyright 2019 Noel Welsh
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
// import java.awt.image.BufferedImage
// import javax.imageio.{IIOImage,ImageIO,ImageWriter,ImageTypeSpecifier}
// import javax.imageio.metadata.IIOMetadataNode
// import javax.imageio.stream.FileImageOutputStream
import monix.eval.{Task, TaskLift}
import monix.execution.Scheduler
import monix.reactive.{Consumer, Observable}

object Java2dAnimationWriter
    extends AnimationWriter[doodle.java2d.Algebra, Drawing, Frame, Gif] {
  // val imageWriter: IO[ImageWriter] =
  //   IO { ImageIO.getImageWritersByFormatName("gif").next() }

  val gifEncoder: IO[GifEncoder] =
    IO { new GifEncoder() }

  /** Set ImageWriter metadata so that animations render as we expect. Notably
    * erase the previous frame before drawing the current one. */
  // def setImageWriterMetadata(iw: ImageWriter): Unit = {
  //   val typeSpec = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB)
  //   val metadata = iw.getDefaultImageMetadata(typeSpec, null)
  //   val ext = new IIOMetadataNode("GraphicControlExtension")
  //   ext.setAttribute("disposalMethod", "restoreToBackgroundColor")
  //   ext.setAttribute("userInputFlag", "FALSE")
  //   ext.setAttribute("transparentColorFlag", "FALSE")
  //   ext.setAttribute("delayTime", ((1.0 / 60.0) * 100).toInt.toString)
  //   ext.setAttribute("transparentColorIndex", "0")

  //   val root = new IIOMetadataNode("javax_imageio_gif_image_1.0")
  //   root.appendChild(ext)
  //   metadata.mergeTree("javax_imageio_gif_image_1.0", root)
  // }

  def write[A](file: File, frame: Frame, frames: Observable[Picture[A]])(
      implicit s: Scheduler,
      m: Monoid[A]): IO[A] = {
    for {
      ge <- gifEncoder
      _ = ge.start(new FileOutputStream(file))
      _ = ge.setDelay(13)
      a <- frames
        .consumeWith(Consumer.foldLeft(IO(m.empty)) { (accum, picture) =>
          for {
            a <- accum
            result <- doodle.java2d.effect.Java2dWriter
              .renderBufferedImage(frame, picture)
            (bi, a2) = result
            _ = ge.addFrame(bi)
          } yield m.combine(a, a2)
        })
        .to[IO](TaskLift.toIO(Task.catsEffect(s)))
        .flatMap(ioa => ioa)

      _ = ge.finish()
    } yield a
    // for {
    //   iw <- imageWriter
    //   _ = iw.setOutput(new FileImageOutputStream(file))

    //   _ = iw.prepareWriteSequence(null)
    //   _ = setImageWriterMetadata(iw)
    //   a <- frames.consumeWith(Consumer.foldLeft(IO(m.empty)) { (accum, picture) =>
    //                             for {
    //                               a <- accum
    //                               result <- doodle.java2d.effect.Java2dWriter.renderBufferedImage(frame, picture)
    //                               (bi, a2) = result
    //                               _ = iw.writeToSequence(new IIOImage(bi, null, null), null)
    //                             } yield m.combine(a, a2)
    //                           }).to[IO](TaskLift.toIO(Task.catsEffect(s))).flatMap(ioa => ioa)
    //   _ = iw.endWriteSequence()
    //   _ = iw.dispose()
    // } yield a
  }
}
