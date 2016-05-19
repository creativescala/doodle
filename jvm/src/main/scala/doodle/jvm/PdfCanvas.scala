package doodle
package jvm

import doodle.core.{DrawingContext,Image,Point}
import doodle.backend._

import de.erichseifert.vectorgraphics2d.PDFGraphics2D

import java.awt.image.BufferedImage
import java.io.FileOutputStream

object PdfCanvas extends Canvas[Unit] {
  def draw(interpreter: Configuration => Interpreter, image: Image): Unit = {
    val metrics = {
      val buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
      val graphics = buffer.createGraphics()
      FontMetrics(graphics.getFontRenderContext()).boundingBox _
    }
    val dc = DrawingContext.blackLines

    val renderable = interpreter(dc, metrics)(image)
    val bb = renderable.boundingBox
    val screenCenter = Point.cartesian(bb.width / 2, bb.height / 2)

    val graphics = new PDFGraphics2D(0, 0, bb.width, bb.height)

    Java2DCanvas.drawToGraphics2D(graphics, screenCenter, renderable)
    graphics.writeTo(new FileOutputStream("example.pdf"))
  }
}
