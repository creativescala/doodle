package doodle
package algebra
package generic

import cats.data.State
import doodle.core.BoundingBox

trait GenericRaster[G[_], A] extends Raster[A] {
  self: Algebra { type Drawing[U] = Finalized[G, U] } =>

  trait RasterApi {
    def raster(width: Int, height: Int)(f: A => Unit): G[Unit]
  }

  def RasterApi: RasterApi

  def raster(width: Int, height: Int)(f: A => Unit): Finalized[G, Unit] = {
    Finalized.leaf { dc =>
      val bb = BoundingBox.centered(width, height)
      (
        bb,
        State.inspect(_ => RasterApi.raster(width, height)(f))
      )
    }
  }
}
