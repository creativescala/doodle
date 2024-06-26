package doodle
package syntax

import doodle.algebra.Picture
import doodle.algebra.Raster

trait RasterSyntax {
  def raster[Alg <: Raster[A], A](
      width: Int,
      height: Int
  )(f: A => Unit): Picture[Alg, Unit] =
    new Picture[Alg, Unit] {
      def apply(implicit algebra: Alg): algebra.Drawing[Unit] =
        algebra.raster(width, height)(f)
    }
}
