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

  def setOrigin(x: Int, y: Int): Unit

  def setStroke(stroke: Stroke): Unit
  def setFill(color: Color): Unit

  def stroke(): Unit
  def fill(): Unit

  def beginPath(): Unit
  def moveTo(x: Double, y: Double): Unit
  def lineTo(x: Double, y: Double): Unit
  def bezierCurveTo(cp1x: Double, cp1y: Double, cp2x: Double, cp2y: Double, endX: Double, endY: Double): Unit
  def endPath(): Unit

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
