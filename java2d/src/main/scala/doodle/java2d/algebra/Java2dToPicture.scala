package doodle.java2d

import doodle.algebra.FromBufferedImage
import doodle.algebra.ToPicture
import doodle.core.Base64
import doodle.core.format._

import java.awt.image.BufferedImage

/** ToPicture instances for the Java2d backend */
trait Java2dToPicture {
  implicit val bufferedImageToPicture: ToPicture[BufferedImage, Algebra] =
    new ToPicture[BufferedImage, Algebra] {
      def toPicture(in: BufferedImage): Picture[Unit] =
        new Picture[Unit] {
          def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
            algebra.fromBufferedImage(in)
        }
    }

  implicit val base64GifToPicture: ToPicture[Base64[Gif], Algebra] =
    new ToPicture[Base64[Gif], Algebra] {
      def toPicture(in: Base64[Gif]): Picture[Unit] =
        new Picture[Unit] {
          def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
            algebra.fromGifBase64(in)
        }
    }

  implicit val base64PngToPicture: ToPicture[Base64[Png], Algebra] =
    new ToPicture[Base64[Png], Algebra] {
      def toPicture(in: Base64[Png]): Picture[Unit] =
        new Picture[Unit] {
          def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
            algebra.fromPngBase64(in)
        }
    }

  implicit val base64JpgToPicture: ToPicture[Base64[Jpg], Algebra] =
    new ToPicture[Base64[Jpg], Algebra] {
      def toPicture(in: Base64[Jpg]): Picture[Unit] =
        new Picture[Unit] {
          def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
            algebra.fromJpgBase64(in)
        }
    }
}
