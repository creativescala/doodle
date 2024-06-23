package doodle
package algebra

trait Raster extends Algebra {
  def raster[A](width: Int, height: Int)(f: A => Unit): Drawing[Unit]
}

trait RasterConstructor {
  self: BaseConstructor { type Algebra <: Raster } =>

  def raster[A](width: Int, height: Int)(f: A => Unit): Picture[Unit] =
    new Picture[Unit] {
      def apply(implicit algebra: Algebra): algebra.Drawing[Unit] =
        algebra.raster(width, height)(f)
    }
}
