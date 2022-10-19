package doodle.algebra

import java.awt.image.BufferedImage

/** Algebra for converting a BufferedImage to a picture */
trait FromBufferedImage extends Algebra {
  def fromBufferedImage(in: BufferedImage): F[Unit]
}

/** Constructor for FromBufferedImage algebra */
trait FromBufferedImageConstructor {
  self: BaseConstructor { type Algebra <: FromBufferedImage } =>

  def fromBase64(image: BufferedImage): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.F[Unit] =
        algebra.fromBufferedImage(image)
    }
}
