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

import cats.effect.IO
import de.erichseifert.vectorgraphics2d.intermediate.CommandSequence
import de.erichseifert.vectorgraphics2d.pdf.PDFProcessor
import de.erichseifert.vectorgraphics2d.util.PageSize
import doodle.core.BoundingBox
import doodle.core.Color
import doodle.core.Transform
import doodle.core.format._
import doodle.core.{Base64 => B64}
import doodle.effect._
import doodle.java2d.algebra.Algebra
import doodle.java2d.algebra.Java2D

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.{Base64 => JBase64}
import javax.imageio.ImageIO

trait Java2dWriter[Fmt <: Format]
    extends Writer[doodle.java2d.Algebra, Frame, Fmt]
    with Base64[doodle.java2d.Algebra, Frame, Fmt] {
  def format: String

  // Allows formats to control the encoding of the buffered image. Not all
  // formats support all encoding. In particular JPEG doesn't support images
  // with alpha channels as described in https://bugs.openjdk.java.net/browse/JDK-8119048
  def makeImage(w: Int, h: Int): BufferedImage

  def write[A](file: File, picture: Picture[A]): IO[A] = {
    write(file, Frame.default.withSizedToPicture(20), picture)
  }

  def write[A](file: File, frame: Frame, picture: Picture[A]): IO[A] = {
    for {
      result <- Java2dWriter.renderBufferedImage(
        frame.size,
        frame.center,
        frame.background,
        picture
      )(makeImage _)
      (bi, a) = result
      _ = ImageIO.write(bi, format, file)
    } yield a
  }

  def base64[A](frame: Frame, image: Picture[A]): IO[(A, B64[Fmt])] =
    for {
      output <- IO.pure(new ByteArrayOutputStream())
      value <- writeToOutput(output, frame, image)
      base64 = JBase64.getEncoder.encodeToString(output.toByteArray)
    } yield (value, B64[Fmt](base64))

  def base64[A](image: Picture[A]): IO[(A, B64[Fmt])] =
    base64(Frame.default.withSizedToPicture(20), image)

  private def writeToOutput[A](
      output: OutputStream,
      frame: Frame,
      picture: Picture[A]
  ): IO[A] = {
    for {
      result <- Java2dWriter.renderBufferedImage(
        frame.size,
        frame.center,
        frame.background,
        picture
      )(makeImage _)
      (bi, a) = result
      _ <- IO {
        ImageIO.write(bi, format, output)
        output.flush()
        output.close()
      }
    } yield a
  }

}
object Java2dWriter {
  def renderBufferedImage[A](
      size: Size,
      center: Center,
      background: Option[Color],
      picture: Picture[A]
  )(makeImage: (Int, Int) => BufferedImage): IO[(BufferedImage, A)] =
    for {
      rendered <- renderGraphics2D(size, center, background, picture) { bb =>
        IO {
          val (w, h) = Java2d.size(bb, size)
          val image = makeImage(w.toInt, h.toInt)

          (Java2d.setup(image.createGraphics()), image)
        }
      }
      (image, a) = rendered
    } yield (image, a)

  private[java2d] def renderGraphics2D[A, I](
      size: Size,
      center: Center,
      background: Option[Color],
      picture: Picture[A]
  )(graphicsContext: BoundingBox => IO[(Graphics2D, I)]): IO[(I, A)] =
    for {
      gc <- IO {
        val bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        Java2d.setup(bi.createGraphics())
      }
      drawing <- IO { picture(Algebra(gc)) }
      (bb, rdr) = drawing.runA(List.empty).value
      (_, fa) = rdr.run(Transform.identity).value
      (r, a) = fa.run.value
      (width, height) = Java2d.size(bb, size)
      tx = Java2d.transform(bb, width, height, center)
      contextWithImage <- graphicsContext(bb)
      (gc, image) = contextWithImage
      _ = background.foreach { c =>
        gc.setColor(Java2D.toAwtColor(c))
        gc.fillRect(0, 0, width.toInt, height.toInt)
      }
      _ = Java2d.render(gc, r, tx)
    } yield (image, a)

}
object Java2dGifWriter extends Java2dWriter[Gif] {
  val format = "gif"

  def makeImage(width: Int, height: Int): BufferedImage =
    new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
}
object Java2dPngWriter extends Java2dWriter[Png] {
  val format = "png"

  def makeImage(width: Int, height: Int): BufferedImage =
    new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
}
object Java2dJpgWriter extends Java2dWriter[Jpg] {
  val format = "jpeg"

  def makeImage(width: Int, height: Int): BufferedImage =
    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
}

object Java2dPdfWriter extends Java2dWriter[Pdf] {
  val format = "pdf"

  def makeImage(width: Int, height: Int): BufferedImage =
    new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

  import de.erichseifert.vectorgraphics2d.VectorGraphics2D

  private def renderVectorCommands[A](
      frame: Frame,
      picture: Picture[A]
  ): IO[((CommandSequence, BoundingBox), A)] =
    for {
      rendered <- Java2dWriter.renderGraphics2D(
        frame.size,
        frame.center,
        frame.background,
        picture
      ) { bb =>
        IO {
          val vectorGraphics = new VectorGraphics2D()
          (vectorGraphics, (vectorGraphics, bb))
        }
      }
      ((image, bounds), a) = rendered
    } yield ((image.getCommands, bounds), a)

  override def write[A](file: File, frame: Frame, picture: Picture[A]): IO[A] =
    for {
      result <- renderVectorCommands(frame, picture)
      ((commands, bounds), value) = result
      _ <- IO {
        val (width, height) = Java2d.size(bounds, frame.size)

        val pdfProcessor = new PDFProcessor(true)
        val doc =
          pdfProcessor.getDocument(commands, new PageSize(width, height))
        doc.writeTo(new FileOutputStream(file))
      }
    } yield value
}
