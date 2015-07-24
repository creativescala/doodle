package doodle
package backend

import doodle.core._

/**
  * An interpreter gives meaning to an Image, usually by drawing it on a Canvas
  *
  * This is a simplified type class pattern. As we only ever draw Images we
  * don't need the generic parameter usually used in a type class.
  */
trait Interpreter {
  def draw(image: Image, canvas: Canvas): Unit = {
    val center = image.boundingBox.center
    canvas.setOrigin(center.x.toInt, center.y.toInt)
    draw(image, canvas, DrawingContext.blackLines, Vec.zero)
  }

  def draw(image: Image, canvas: Canvas, context: DrawingContext, origin: Vec): Unit  
}
