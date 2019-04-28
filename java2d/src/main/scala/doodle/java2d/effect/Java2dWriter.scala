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
import doodle.algebra.Image
import doodle.core.Transform
import doodle.effect._
import doodle.java2d.algebra.Algebra
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

trait Java2dWriter[Format] extends Writer[Algebra, Drawing, Frame, Format] {
  def format: String

  def write[A, Alg >: Algebra](file: File,
                               image: Image[Alg, Drawing, A]): IO[A] = {
    write(file, Frame.fitToImage(), image)
  }

  def write[A, Alg >: Algebra](file: File,
                               frame: Frame,
                               image: Image[Alg, Drawing, A]): IO[A] = {
    for {
      result <- Java2dWriter.renderBufferedImage(frame, image)
      (bi, a) = result
      _ = ImageIO.write(bi, format, file)
    } yield a
  }
}
object Java2dWriter {
  def renderBufferedImage[A, Alg >: Algebra](frame: Frame,
                                             image: Image[Alg, Drawing, A]): IO[(BufferedImage,A)] =
    for {
      drawing <- IO { image(Algebra()) }
      (bb, rdr) = drawing.runA(List.empty).value
      bi <- IO {
        frame.size match {
          case Size.FitToImage(border) =>
            new BufferedImage(bb.width.toInt + border,
                              bb.height.toInt + border,
                              BufferedImage.TYPE_INT_ARGB)

          case Size.FixedSize(w, h) =>
            new BufferedImage(w.toInt, h.toInt, BufferedImage.TYPE_INT_ARGB)

          case Size.FullScreen => ???
        }
      }
      gc = bi.createGraphics()
      (tx, fa) = rdr.run(Transform.identity).value
      (r, a) = fa.run
      _ = Java2d.render(gc, bb, r, bb.width, bb.height, frame.center)
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
