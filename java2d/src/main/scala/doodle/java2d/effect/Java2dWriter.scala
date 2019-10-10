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
import doodle.core.Transform
import doodle.effect._
import doodle.java2d.algebra.Algebra
import java.awt.image.BufferedImage
import java.io.{ByteArrayOutputStream, File, FileOutputStream, OutputStream}
import java.util.Base64
import javax.imageio.ImageIO

trait Java2dWriter[Format]
    extends Writer[doodle.java2d.Algebra, Drawing, Frame, Format] {
  def format: String

  def write[A](file: File, picture: Picture[A]): IO[A] = {
    write(file, Frame.fitToPicture(), picture)
  }

  def write[A](file: File, frame: Frame, picture: Picture[A]): IO[A] =
    writeToOutput(new FileOutputStream(file), frame, picture)

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

  def base64[A](image: Picture[A]): IO[(A, String)] = for {
    output <- IO.pure(new ByteArrayOutputStream())
    value <- writeToOutput(output, Frame.fitToPicture(), image)
    base64 = Base64.getEncoder.encodeToString(output.toByteArray)
  } yield (value, base64)

}
object Java2dWriter {
  def renderBufferedImage[A](frame: Frame,
                             picture: Picture[A]): IO[(BufferedImage, A)] =
    for {
      drawing <- IO { picture(Algebra()) }
      (bb, rdr) = drawing.runA(List.empty).value
      bi <- IO {
        val (w, h) = Java2d.size(bb, frame.size)
        new BufferedImage(w.toInt,
                          h.toInt,
                          BufferedImage.TYPE_INT_ARGB)
      }
      gc = Java2d.setup(bi.createGraphics())
      (_, fa) = rdr.run(Transform.identity).value
      (r, a) = fa.run.value
      tx = Java2d.transform(bb,
                            bi.getWidth.toDouble,
                            bi.getHeight.toDouble,
                            frame.center)
      _ = Java2d.render(gc, r, tx)
    } yield (bi, a)
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
