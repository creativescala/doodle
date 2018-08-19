package doodle
package java2d
package engine

import cats.effect.IO
import doodle.algebra.Image
import doodle.algebra.generic._
import doodle.core.Point
import doodle.engine._
import doodle.java2d.algebra.Algebra
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object Java2dGifWriter extends Writer[Algebra,Drawing,Writer.Gif] {
  def write[A, Alg >: Algebra](file: File, frame: Frame, image: Image[Alg,Drawing,A]): IO[A] = {
    val drawing = image(Algebra())
    val (bb, ctx) = drawing(DrawingContext.default)

    for {
      image <- IO {
        frame.size match {
          case Size.FitToImage(border) =>
            new BufferedImage(bb.width.toInt + border, bb.height.toInt + border, BufferedImage.TYPE_INT_ARGB)

          case Size.FixedSize(w, h) =>
            new BufferedImage(w.toInt, h.toInt, BufferedImage.TYPE_INT_ARGB)

          case Size.FullScreen => ???
        }
      }
     gc = image.createGraphics()
     tx = Transform.logicalToScreen(image.getWidth.toDouble, image.getHeight.toDouble)
      a <- ctx((gc, tx))(Point.zero)
      _ = ImageIO.write(image, "gif", file)
    } yield a
  }
}
