package doodle
package jvm

import doodle.core._
import doodle.core.transform.Transform
import doodle.backend.{BoundingBox, Canvas}
import java.awt.Graphics2D

final class Java2DCanvas(graphics: Graphics2D, center: Point, screenCenter: Point) extends Canvas {

  // Convert from canvas coordinates to screen coordinates.
  //
  // Shift the center of the bounding box to the origin.
  //
  // Reflect around the Y axis as the canvas Y coordinate is reversed
  // compared to the Java2D Y axis.
  //
  // Then recenter the canvas to the center of the screen.
  graphics.transform(
    Java2D.toAffineTransform(
      Transform.translate(-center.x, -center.y)
        .andThen(Transform.horizontalReflection)
        .andThen(Transform.translate(screenCenter.x, screenCenter.y))
    )
  )

  def openPath(context: DrawingContext, elements: List[PathElement]): Unit = {
    val path = Java2D.toPath2D(elements)
    Java2D.strokeAndFill(graphics, path, context)
  }

  def closedPath(context: DrawingContext, elements: List[PathElement]): Unit = {
    val path = Java2D.toPath2D(elements)
    path.closePath()
    Java2D.strokeAndFill(graphics, path, context)
  }

  def text(context: DrawingContext,
           tx: Transform,
           boundingBox: BoundingBox,
           characters: String): Unit = {
    // We have to do a few different transformations here:
    //
    // - The canvas Y coordinate is reversed with respect to the
    // screen Y coordinate, so normally we have to reverse
    // coordinates. However `drawString` will draw text oriented
    // correctly on the screen with need to reverse our reverse.
    //
    // - `drawString` draws from the bottom left corner of the text
    // while the origin of the bounding box is the center of the text.
    // Thus we need to translate to the bottom left corner.
    val bottomLeft = Transform.translate(-boundingBox.width/2, -boundingBox.height/2)
    val fullTx = Transform.horizontalReflection andThen tx andThen bottomLeft
    val currentTx = graphics.getTransform()
    graphics.transform(Java2D.toAffineTransform(fullTx))
    context.stroke.foreach { s =>
      Java2D.setStroke(graphics, s)
    }
    context.fillColor.foreach { f =>
      Java2D.setFill(graphics, f)
    }
    context.font map { f =>
      graphics.setFont(FontMetrics.toJFont(f))
      graphics.drawString(characters, 0, 0)
    }
    graphics.setTransform(currentTx)
  }
}
