package doodle
package backend

import doodle.core.{DrawingContext,Vec}

/**
  * An interpreter gives meaning to an Image, usually by drawing it on a Canvas
  *
  * This is a simplified type class pattern. As we only ever draw Images we
  * don't need the generic parameter usually used in a type class.
  */
trait Interpreter {
  import doodle.core

  /** Draw an Image on a Canvas of the given size with origin optionally
    * translated to the given point */
  def draw(image: core.Image, canvas: Canvas, width: Int, height: Int, origin: Vec = Vec.zero): Unit = {
    canvas.setSize(width, height)
    canvas.setOrigin(origin.x.ceil.toInt, origin.y.ceil.toInt)
    draw(image, canvas, DrawingContext.blackLines, Vec.zero)
  }

  /** Draw an Image centered on the Canvas. */
  def draw(image: core.Image, canvas: Canvas): Unit = {
    val i = Image.compile(image, DrawingContext.blackLines)
    val bb = i.boundingBox
    draw(image, canvas, bb.width.ceil.toInt, bb.height.ceil.toInt, bb.center)
  }

  def draw(image: core.Image, canvas: Canvas, context: DrawingContext, origin: Vec): Unit =
    draw(Image.compile(image, context), canvas, origin)

  def draw(image: Image, canvas: Canvas, origin: Vec): Unit
}
