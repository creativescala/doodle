package doodle
package backend

import doodle.core.{Color, Stroke}

/**
  * A canvas interface abstracts over the different vector drawing libraries we
  * support. It provides a low-level imperative API, the higher-level
  * declarative APIs can compile into.
  *
  * We implement this as an open trait (vs sealed trait) as the different
  * implementations live in different packages and type classes don't work well
  * with stateful interfaces.
  *
  * We assume the origin is centered in the drawing context, and coordinates
  * follow the standard cartesian layout. The Canvas implementation is
  * responsible for translating this coordinate system to whatever is used
  * by the specific implementation it draws to.
  *
  * Callers can change the origin to another location to, for example, center an
  * image in the canvas (if that image is itself not centered on the origin)
  */
trait Canvas {
  /** Set the size of the canvas.
    *
    * Should only be set before drawing takes place, and before calling
    * setOrigin as calls to setOrigin will often use the current size to
    * calculate offsets in the implementation specific coordinate system.
    * 
    * The Canvas may ignore this if it is not capable of changing its size. */
  def setSize(width: Int, height: Int): Unit

  /** Translate the origin by the given x and y amount. Should only be set before
    * drawing takes place.*/
  def setOrigin(x: Int, y: Int): Unit

  /** Fills the entire canvas with the given color */
  def clear(color: Color): Unit

  def setStroke(stroke: Stroke): Unit
  def setFill(color: Color): Unit

  def stroke(): Unit
  def fill(): Unit

  def beginPath(): Unit
  def moveTo(x: Double, y: Double): Unit
  def lineTo(x: Double, y: Double): Unit
  def bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, endX: Double, endY: Double): Unit
  def endPath(): Unit

  /** Set a callback that will be called when the canvas is ready to display to a
    * new frame. This will generally occur at 60fps. There can only be a single
    * callback registered at any one time and there is no way to cancel a
    * callback once it is registered.
    *
    * The callback is passed a monotically increasing value representing the
    * current time in undefined units.
    */
  def setAnimationFrameCallback(callback: Double => Unit): Unit

  // Convenience functions

  def circle(centerX: Double, centerY: Double, radius: Double): Unit = {
    // See http://spencermortensen.com/articles/bezier-circle/ for approximation
    // of a circle with a Bezier curve.
    val c = 0.551915024494
    val cR = c * radius
    beginPath()
    moveTo(centerX, centerY + radius)
    bezierCurveTo(centerX + cR, centerY + radius,
                  centerX + radius, centerY + cR,
                  centerX + radius, centerY)
    bezierCurveTo(centerX + radius, centerY - cR,
                  centerX + cR, centerY - radius,
                  centerX, centerY - radius)
    bezierCurveTo(centerX - cR, centerY - radius,
                  centerX - radius, centerY - cR,
                  centerX - radius, centerY)
    bezierCurveTo(centerX - radius, centerY + cR,
                  centerX - cR, centerY + radius,
                  centerX, centerY + radius)
    endPath()
  }
  def rectangle(left: Double, top: Double, width: Double, height: Double): Unit = {
    beginPath()
    moveTo(left        , top)
    lineTo(left + width, top)
    lineTo(left + width, top - height)
    lineTo(left        , top - height)
    lineTo(left        , top)
    endPath()
  }
}
