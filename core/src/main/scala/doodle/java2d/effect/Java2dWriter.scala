package doodle
package java2d
package effect

import cats.effect.IO
import doodle.algebra.Image
import doodle.core.Transform
import doodle.effect._
import doodle.java2d.algebra.{Algebra, Java2D}
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

trait Java2dWriter[Format] extends Writer[Algebra, Drawing, Format] {
  def format: String

  def write[A, Alg >: Algebra](file: File,
                               frame: Frame,
                               image: Image[Alg, Drawing, A]): IO[A] = {
    for {
      drawing <- IO { image(Algebra()) }
      (bb, rdr) = drawing.runA(List.empty).value
      image <- IO {
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
      gc = image.createGraphics()
      (r, _, a) = rdr.run((), Transform.identity).value
      _ = Java2D.renderCentered(gc, bb, r, bb.width, bb.height)
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
