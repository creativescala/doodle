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

import java.awt.Graphics2D

import cats.effect.IO
import doodle.core.Transform
import doodle.effect._
import doodle.java2d.algebra.Algebra
import java.awt.image.BufferedImage
import java.io.{ByteArrayOutputStream, File, FileOutputStream, OutputStream}
import java.util.{Base64 => JBase64}
import de.erichseifert.vectorgraphics2d.intermediate.CommandSequence
import de.erichseifert.vectorgraphics2d.pdf.PDFProcessor
import de.erichseifert.vectorgraphics2d.util.PageSize
import doodle.algebra.generic.BoundingBox
import javax.imageio.ImageIO

trait Java2dWriter[Format]
    extends Writer[doodle.java2d.Algebra, Drawing, Frame, Format]
    with Base64[doodle.java2d.Algebra, Drawing, Frame, Format]{
  def format: String

  def write[A](file: File, picture: Picture[A]): IO[A] = {
    write(file, Frame.fitToPicture(), picture)
  }

  def write[A](file: File, frame: Frame, picture: Picture[A]): IO[A] = {
    for {
      result <- Java2dWriter.renderBufferedImage(frame, picture)
      (bi, a) = result
      _ = ImageIO.write(bi, format, file)
    } yield a
  }

  def base64[A](frame: Frame, image: Picture[A]): IO[(A, String)] =
    for {
      output <- IO.pure(new ByteArrayOutputStream())
      value <- writeToOutput(output, frame, image)
      base64 = JBase64.getEncoder.encodeToString(output.toByteArray)
    } yield (value, base64)

  def base64[A](image: Picture[A]): IO[(A, String)] =
    base64(Frame.fitToPicture(), image)

  private def writeToOutput[A](output: OutputStream, frame: Frame, picture: Picture[A]): IO[A] = {
    for {
      result <- Java2dWriter.renderBufferedImage(frame, picture)
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
  def renderBufferedImage[A](frame: Frame,
                             picture: Picture[A]): IO[(BufferedImage, A)] =
    for {
      rendered <- renderGraphics2D(frame, picture, bb => IO {
        val (w, h) = Java2d.size(bb, frame.size)

        val image = new BufferedImage(w.toInt,
          h.toInt,
          BufferedImage.TYPE_INT_ARGB)

        (Java2d.setup(image.createGraphics()), image)
      })
      (image, a) = rendered
    } yield (image, a)

  private[java2d] def renderGraphics2D[A, I](frame: Frame,
                             picture: Picture[A],
                             graphicsContext: BoundingBox => IO[(Graphics2D, I)]): IO[(I, A)] =
    for {
      gc <- IO {
        val bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        Java2d.setup(bi.createGraphics())
      }
      drawing <- IO { picture(Algebra(gc)) }
      (bb, rdr) = drawing.runA(List.empty).value
      (_, fa) = rdr.run(Transform.identity).value
      (r, a) = fa.run.value
      tx = {
        val (width, height) = Java2d.size(bb, frame.size)

        Java2d.transform(bb,
          width.toDouble,
          height.toDouble,
          frame.center)
      }
      contextWithImage <- graphicsContext(bb)
      (gc, image) = contextWithImage
      _ = Java2d.render(gc, r, tx)
    } yield (image, a)

}
object Java2dGifWriter extends Java2dWriter[Writer.Gif] {
  val format = "gif"
}
object Java2dPngWriter extends Java2dWriter[Writer.Png] {
  val format = "png"
}
object Java2dJpgWriter extends Java2dWriter[Writer.Jpg] {
  val format = "jpg"
}

object Java2dPdfWriter extends Java2dWriter[Writer.Pdf] {
  val format = "pdf"

  import de.erichseifert.vectorgraphics2d.VectorGraphics2D

  private def renderVectorCommands[A](frame: Frame,
                              picture: Picture[A]): IO[((CommandSequence, BoundingBox), A)] =
    for {
      rendered <- Java2dWriter.renderGraphics2D(frame, picture, bb => IO {
        val vectorGraphics = new VectorGraphics2D()
        (vectorGraphics, (vectorGraphics, bb))
      })
      ((image, bounds), a) = rendered
    } yield ((image.getCommands, bounds), a)

  override def write[A](file: File, frame: Frame, picture: Picture[A]): IO[A] =
    for {
      result <- renderVectorCommands(frame, picture)
      ((commands, bounds), value) = result
      _ <- IO {
        val (width, height) = Java2d.size(bounds, frame.size)

        val pdfProcessor = new PDFProcessor(true)
        val doc = pdfProcessor.getDocument(commands, new PageSize(width, height))
        doc.writeTo(new FileOutputStream(file))
      }
    } yield value
}
