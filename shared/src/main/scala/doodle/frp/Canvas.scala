package doodle
package frp

import doodle.backend

object Canvas {
  /** Create an infinite event stream with one event every animation frame. The
    * values are timestamp. */
  def animationFrame(implicit c: backend.Canvas): Event[Double] = {
    val (evt, callback) = Event.fromCallback[Double]
    c.setAnimationFrameCallback(callback)
    evt
  }

}
