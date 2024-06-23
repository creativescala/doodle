package doodle.canvas.algebra

import doodle.algebra.Algebra
import doodle.algebra.generic.*

trait Raster extends GenericRaster[CanvasDrawing] {
  self: Algebra { type Drawing[Unit] = Finalized[CanvasDrawing, Unit] } =>
  object RasterApi extends RasterApi {
    def raster[A](width: Int, height: Int)(
        f: A => Unit
    ): CanvasDrawing[Unit] = {
      CanvasDrawing.raster(width, height)(f)
    }
  }
}
