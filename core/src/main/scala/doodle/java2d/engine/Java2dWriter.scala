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

trait Java2dWriter[Format] extends Writer[Algebra,Drawing,Format] {
  def format: String

  def write[A, Alg >: Algebra](file: File, frame: Frame, image: Image[Alg,Drawing,A]): IO[A] = {
    for {
      drawing <- IO { image(Algebra()) }
      (bb, ctx) = drawing(DrawingContext.default)
      image   <- IO {
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
      _ = ImageIO.write(image, format, file)
    } yield a
  }
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
